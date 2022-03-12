package com.gasparaiciukas.owntrainer.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException
import java.time.LocalDate

class FakeDiaryRepository : DiaryRepository {

    private val diaryEntries = mutableListOf<DiaryEntry>()
    private val diaryEntryMealCrossRefs = mutableListOf<DiaryEntryMealCrossRef>()
    private val meals = mutableListOf<Meal>()
    private val mealsWithFoodEntries = mutableListOf<MealWithFoodEntries>()
    private var foodEntries = mutableListOf<FoodEntry>()

    fun setFoodEntries(foodEntries: MutableList<FoodEntry>) {
        this.foodEntries = foodEntries
    }

    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntry) {
        diaryEntries.add(diaryEntry)
    }

    override suspend fun removeDiaryEntry(year: Int, dayOfYear: Int) {
        diaryEntries.removeIf { it.year == year && it.dayOfYear == dayOfYear }
    }

    override fun getDiaryEntry(year: Int, dayOfYear: Int): Flow<DiaryEntry> {
        val date = LocalDate.ofYearDay(year, dayOfYear)
        val diaryEntry = DiaryEntry(
            year = date.year,
            dayOfYear = date.dayOfYear,
            dayOfWeek = date.dayOfWeek.value,
            monthOfYear = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )
        return if (diaryEntries.contains(diaryEntry)) {
            MutableLiveData(diaryEntry).asFlow()
        } else {
            throw RuntimeException("getDiaryEntry() couldn't find diary entry")
        }
    }

    override fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals> {
        val diaryEntry = getDiaryEntry(year, dayOfYear).asLiveData().getOrAwaitValueTest()
        val diaryEntryMeals = mutableListOf<Meal>()
        for (meal in meals) {
            val crossRef = DiaryEntryMealCrossRef(
                diaryEntries.first { it.year == year && it.dayOfYear == dayOfYear }.diaryEntryId,
                meal.mealId)
            if (diaryEntryMealCrossRefs.contains(crossRef)) {
                diaryEntryMeals.add(meal)
            }
        }
        return MutableLiveData(DiaryEntryWithMeals(
            diaryEntry,
            diaryEntryMeals
        )).asFlow()
    }

    override suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef) {
        diaryEntryMealCrossRefs.add(crossRef)
        println(diaryEntryMealCrossRefs)
    }

    override suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int) {
        diaryEntryMealCrossRefs.removeIf { it.diaryEntryId == diaryEntryId && it.mealId == mealId }
    }

    override fun getMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>> {
        return MutableLiveData(mealsWithFoodEntries).asFlow()
    }

    override suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries {
        return getMealsWithFoodEntries().asLiveData().getOrAwaitValueTest().first { it.meal.mealId == id }
    }

    override suspend fun addMeal(meal: Meal) {
        meals.add(meal)
        mealsWithFoodEntries.add(MealWithFoodEntries(meal, foodEntries))
    }

    override suspend fun deleteMealById(mealId: Int) {
        meals.removeIf { it.mealId == mealId }
        mealsWithFoodEntries.removeIf { it.meal.mealId == mealId }
    }
}