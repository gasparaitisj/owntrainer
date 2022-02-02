package com.gasparaiciukas.owntrainer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal")
    fun getAll(): Flow<List<Meal>>

    @Insert
    suspend fun insertAll(vararg meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Transaction
    @Query("SELECT * FROM meal")
    fun getMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>>

    @Transaction
    @Query("SELECT * FROM meal WHERE mealId = :id")
    suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries
}