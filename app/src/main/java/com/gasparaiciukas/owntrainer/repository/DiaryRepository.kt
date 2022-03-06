package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.*
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    suspend fun insertDiaryEntry(diaryEntry: DiaryEntry)

    suspend fun removeDiaryEntry(year: Int, dayOfYear: Int)

    fun getDiaryEntry(year: Int, dayOfYear: Int): Flow<DiaryEntry>

    fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals>

    suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef)

    suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int)

    fun getMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>>

    suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries

    suspend fun addMeal(meal: Meal)

    suspend fun deleteMealById(mealId: Int)
}