package com.gasparaiciukas.owntrainer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal")
    fun getAllMeals(): Flow<List<Meal>>

    @Insert
    suspend fun insertMeal(meal: Meal)

    @Query("DELETE FROM meal WHERE mealId = :id")
    suspend fun deleteMealById(id: Int)

    @Transaction
    @Query("SELECT * FROM meal")
    fun getMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>>

    @Transaction
    @Query("SELECT * FROM meal WHERE mealId = :id")
    suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries
}