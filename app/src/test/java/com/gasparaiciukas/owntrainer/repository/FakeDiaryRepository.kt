package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.time.LocalDate

@ExperimentalCoroutinesApi
class FakeDiaryRepository : DiaryRepository {

    private val diaryEntries = mutableListOf<DiaryEntry>()
    private val diaryEntryMealCrossRefs = mutableListOf<DiaryEntryMealCrossRef>()
    private val meals = mutableListOf<Meal>()
    private val mealsWithFoodEntries = mutableListOf<MealWithFoodEntries>()
    private var foodEntries = mutableListOf<FoodEntry>()
    private var shouldReturnNetworkError = false

    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntry) {
        diaryEntries.add(diaryEntry)
    }

    override suspend fun deleteDiaryEntry(year: Int, dayOfYear: Int) {
        diaryEntries.removeIf { it.year == year && it.dayOfYear == dayOfYear }
    }

    override fun getDiaryEntry(year: Int, dayOfYear: Int): Flow<DiaryEntry?> {
        val date = LocalDate.ofYearDay(year, dayOfYear)
        val diaryEntry = DiaryEntry(
            year = date.year,
            dayOfYear = date.dayOfYear,
            dayOfWeek = date.dayOfWeek.value,
            monthOfYear = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )
        return if (diaryEntries.any { d -> d.year == year && d.dayOfYear == dayOfYear }) {
            MutableStateFlow(diaryEntry)
        } else {
            MutableStateFlow(null)
        }
    }

    override fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals?> {
        var diaryEntry: DiaryEntry? = null
        runTest {
            diaryEntry = getDiaryEntry(year, dayOfYear).first()
        }
        val diaryEntryMeals = mutableListOf<Meal>()
        for (meal in meals) {
            val crossRef = DiaryEntryMealCrossRef(
                diaryEntries.first { it.year == year && it.dayOfYear == dayOfYear }.diaryEntryId,
                meal.mealId
            )
            if (diaryEntryMealCrossRefs.contains(crossRef)) {
                diaryEntryMeals.add(meal)
            }
        }
        diaryEntry?.let {
            return MutableStateFlow(
                DiaryEntryWithMeals(
                    it,
                    diaryEntryMeals
                )
            )
        }
        return MutableStateFlow(null)
    }

    override suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef) {
        diaryEntryMealCrossRefs.add(crossRef)
    }

    override suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int) {
        diaryEntryMealCrossRefs.removeIf { it.diaryEntryId == diaryEntryId && it.mealId == mealId }
    }

    override fun getAllMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>?> {
        return MutableStateFlow(mealsWithFoodEntries)
    }

    override suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries? {
        return getAllMealsWithFoodEntries().first()
            ?.first { it.meal.mealId == id }
    }

    override suspend fun insertMeal(meal: Meal) {
        meals.add(meal)
        mealsWithFoodEntries.add(MealWithFoodEntries(meal, foodEntries))
    }

    override suspend fun deleteMealById(mealId: Int) {
        meals.removeIf { it.mealId == mealId }
        mealsWithFoodEntries.removeIf { it.meal.mealId == mealId }
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getFoods(
        query: String,
        dataType: String?,
        numberOfResultsPerPage: Int?,
        pageSize: Int?,
        pageNumber: Int?,
        sortBy: String?,
        sortOrder: String?,
        requireAllWords: Boolean?
    ): Resource<GetResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Error", null)
        } else {
            Resource.success(
                GetResponse(
                    totalHits = 0,
                    currentPage = 0,
                    totalPages = 0,
                    foods = mutableListOf()
                )
            )
        }
    }

    override fun getAllFoodEntries(): Flow<List<FoodEntry>> {
        return MutableStateFlow(foodEntries)
    }

    override suspend fun insertFood(foodEntry: FoodEntry) {
        foodEntries.add(foodEntry)
    }

    override suspend fun deleteFoodById(foodEntryId: Int) {
        foodEntries.removeIf { it.foodEntryId == foodEntryId }
    }
}