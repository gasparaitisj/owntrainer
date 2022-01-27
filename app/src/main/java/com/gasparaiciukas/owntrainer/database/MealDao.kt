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
    fun getMealWithFoodEntries(): Flow<List<MealWithFoodEntries>>
}