package com.gasparaiciukas.owntrainer.database

import androidx.room.*

@Dao
interface MealWithFoodEntriesDao {
    @Transaction
    @Query("SELECT * FROM meal")
    fun getMealWithFoodEntries(): List<MealWithFoodEntries>
}