package com.gasparaiciukas.owntrainer.utils.repository

import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntry
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryDao
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryMealCrossRef
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryWithMealsDao
import com.gasparaiciukas.owntrainer.utils.database.FoodEntry
import com.gasparaiciukas.owntrainer.utils.database.FoodEntryDao
import com.gasparaiciukas.owntrainer.utils.database.Meal
import com.gasparaiciukas.owntrainer.utils.database.MealDao
import com.gasparaiciukas.owntrainer.utils.network.GetResponse
import com.gasparaiciukas.owntrainer.utils.network.GetService
import com.gasparaiciukas.owntrainer.utils.network.Resource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

@Singleton
class DiaryRepository @Inject constructor(
    private val diaryEntryDao: DiaryEntryDao,
    private val diaryEntryWithMealsDao: DiaryEntryWithMealsDao,
    private val mealDao: MealDao,
    private val foodEntryDao: FoodEntryDao,
    private val getService: GetService,
) {
    suspend fun insertDiaryEntry(diaryEntry: DiaryEntry) =
        diaryEntryDao.insertDiaryEntry(diaryEntry)

    suspend fun deleteDiaryEntry(year: Int, dayOfYear: Int) =
        diaryEntryDao.deleteDiaryEntryByYearAndDayOfYear(year, dayOfYear)

    fun getDiaryEntry(year: Int, dayOfYear: Int) =
        diaryEntryDao.getDiaryEntryByYearAndDayOfYear(year, dayOfYear)

    fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int) =
        diaryEntryWithMealsDao.getDiaryEntryWithMeals(year, dayOfYear)

    suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef) =
        diaryEntryWithMealsDao.insertDiaryEntryMealCrossRef(crossRef)

    suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int) =
        diaryEntryWithMealsDao.deleteDiaryEntryMealCrossRef(diaryEntryId, mealId)

    fun getAllMealsWithFoodEntries() =
        mealDao.getAllMealsWithFoodEntries()

    suspend fun getMealWithFoodEntriesById(id: Int) =
        mealDao.getMealWithFoodEntriesById(id)

    suspend fun insertMeal(meal: Meal) =
        mealDao.insertMeal(meal)

    suspend fun deleteMealById(mealId: Int) =
        mealDao.deleteMealById(mealId)

    suspend fun getFoods(
        query: String,
        dataType: String? = null,
        numberOfResultsPerPage: Int? = null,
        pageSize: Int? = null,
        pageNumber: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        requireAllWords: Boolean? = null,
    ): Resource<GetResponse> {
        return try {
            val response = getService.getFoods(
                query = query,
                dataType = dataType,
                pageSize = pageSize,
                numberOfResultsPerPage = numberOfResultsPerPage,
                pageNumber = pageNumber,
                sortBy = sortBy,
                sortOrder = sortOrder,
                requireAllWords = requireAllWords,
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error(msg = response.message())
            } else {
                Resource.error(msg = response.message())
            }
        } catch (e: Exception) {
            Timber.e(e)
            Resource.error(msgRes = R.string.network_error)
        }
    }

    fun getAllFoodEntries(): Flow<List<FoodEntry>> =
        foodEntryDao.getAllFoodEntries()

    suspend fun insertFood(foodEntry: FoodEntry) {
        foodEntryDao.insertFoodEntry(foodEntry)
    }

    suspend fun deleteFoodById(foodEntryId: Int) {
        foodEntryDao.deleteFoodEntryById(foodEntryId)
    }
}
