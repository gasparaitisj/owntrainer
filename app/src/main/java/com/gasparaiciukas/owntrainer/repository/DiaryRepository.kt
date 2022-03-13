package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    suspend fun insertDiaryEntry(diaryEntry: DiaryEntry)

    suspend fun deleteDiaryEntry(year: Int, dayOfYear: Int)

    fun getDiaryEntry(year: Int, dayOfYear: Int): Flow<DiaryEntry>

    fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals>

    suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef)

    suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int)

    fun getMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>>

    suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries

    suspend fun insertMeal(meal: Meal)

    suspend fun deleteMealById(mealId: Int)

    suspend fun getFoods(query: String): Resource<GetResponse>

    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    suspend fun insertFood(foodEntry: FoodEntry)

    suspend fun deleteFoodById(foodEntryId: Int)
}