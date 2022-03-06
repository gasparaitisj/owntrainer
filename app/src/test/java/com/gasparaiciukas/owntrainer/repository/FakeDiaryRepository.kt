package com.gasparaiciukas.owntrainer.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.gasparaiciukas.owntrainer.database.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FakeDiaryRepository : DiaryRepository {

    private val diaryEntries = mutableListOf<DiaryEntry>()
    private val diaryEntryMealCrossRefs = mutableListOf<DiaryEntryMealCrossRef>()
    private val mealsWithFoodEntries = mutableListOf<MealWithFoodEntries>()
    private val meals = mutableListOf<Meal>()

    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntry) {
        diaryEntries.add(diaryEntry)
    }

    override suspend fun removeDiaryEntry(year: Int, dayOfYear: Int) {
        diaryEntries.removeIf { it.year == year && it.dayOfYear == dayOfYear }
    }

    override fun getDiaryEntry(year: Int, dayOfYear: Int): Flow<DiaryEntry> {
        val date = LocalDate.ofYearDay(year, dayOfYear)
        return MutableLiveData(DiaryEntry(
            year = date.year,
            dayOfYear = date.dayOfYear,
            dayOfWeek = date.dayOfWeek.value,
            monthOfYear = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )).asFlow()
    }

    override fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals> {
        val date = LocalDate.ofYearDay(year, dayOfYear)
        return MutableLiveData(DiaryEntryWithMeals(
            DiaryEntry(
                year = date.year,
                dayOfYear = date.dayOfYear,
                dayOfWeek = date.dayOfWeek.value,
                monthOfYear = date.monthValue,
                dayOfMonth = date.dayOfMonth
            ),
            listOf()
        )).asFlow()
    }

    override suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef) {
        diaryEntryMealCrossRefs.add(crossRef)
    }

    override suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int) {
        diaryEntryMealCrossRefs.removeIf { it.diaryEntryId == diaryEntryId && it.mealId == mealId }
    }

    override fun getMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>> {
        return MutableLiveData(mealsWithFoodEntries).asFlow()
    }

    override suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries {
        val meal = Meal(
            title = "Title",
            instructions = "Instructions"
        )
        meal.id = id
        return MealWithFoodEntries(
            Meal(
                title = "Title",
                instructions = "Instructions"
            ),
            listOf()
        )
    }

    override suspend fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    override suspend fun deleteMealById(mealId: Int) {
        meals.removeIf { it.id == mealId }
    }
}