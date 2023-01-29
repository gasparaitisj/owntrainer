package com.gasparaiciukas.owntrainer.utils.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
    fun getAllMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>>

    @Transaction
    @Query("SELECT * FROM meal WHERE mealId = :id")
    suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries?
}
