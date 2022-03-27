package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.BuildConfig
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.DefaultGetService
import com.gasparaiciukas.owntrainer.network.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDiaryRepository @Inject constructor(
    private val diaryEntryDao: DiaryEntryDao,
    private val diaryEntryWithMealsDao: DiaryEntryWithMealsDao,
    private val mealDao: MealDao,
    private val foodEntryDao: FoodEntryDao,
    private val defaultGetService: DefaultGetService
): DiaryRepository {
    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntry) =
        diaryEntryDao.insertDiaryEntry(diaryEntry)

    override suspend fun deleteDiaryEntry(year: Int, dayOfYear: Int) =
        diaryEntryDao.deleteDiaryEntryByYearAndDayOfYear(year, dayOfYear)

    override fun getDiaryEntry(year: Int, dayOfYear: Int) =
        diaryEntryDao.getDiaryEntryByYearAndDayOfYear(year, dayOfYear)

    override fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int) =
        diaryEntryWithMealsDao.getDiaryEntryWithMeals(year, dayOfYear)

    override suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef) =
        diaryEntryWithMealsDao.insertDiaryEntryMealCrossRef(crossRef)

    override suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int) =
        diaryEntryWithMealsDao.deleteDiaryEntryMealCrossRef(diaryEntryId, mealId)

    override fun getAllMealsWithFoodEntries() =
        mealDao.getAllMealsWithFoodEntries()

    override suspend fun getMealWithFoodEntriesById(id: Int) =
        mealDao.getMealWithFoodEntriesById(id)

    override suspend fun insertMeal(meal: Meal) =
        mealDao.insertMeal(meal)

    override suspend fun deleteMealById(mealId: Int) =
        mealDao.deleteMealById(mealId)

    override suspend fun getFoods(query: String): Resource<GetResponse> {
        return try {
            val response = defaultGetService.getFoods(BuildConfig.API_KEY, query)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("An unknown error occurred.", null)
            } else {
                Resource.error("An unknown error occurred.", null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't reach the server. Check your internet connection.", null)
        }
    }

    override fun getAllFoodEntries(): Flow<List<FoodEntry>> =
        foodEntryDao.getAllFoodEntries()

    override suspend fun insertFood(foodEntry: FoodEntry) {
        foodEntryDao.insertFoodEntry(foodEntry)
    }

    override suspend fun deleteFoodById(foodEntryId: Int) {
        foodEntryDao.deleteFoodEntryById(foodEntryId)
    }
}