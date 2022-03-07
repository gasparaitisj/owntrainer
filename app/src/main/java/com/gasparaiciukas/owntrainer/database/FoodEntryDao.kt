package com.gasparaiciukas.owntrainer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM foodEntry")
    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    @Insert
    suspend fun insertFoodEntry(foodEntry: FoodEntry)

    @Query("DELETE FROM foodEntry WHERE foodEntryId = :id")
    suspend fun deleteFoodEntryById(id: Int)
}