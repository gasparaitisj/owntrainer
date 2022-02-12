package com.gasparaiciukas.owntrainer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM foodEntry")
    fun getAll(): Flow<List<FoodEntry>>

    @Insert
    suspend fun insertAll(vararg foodEntry: FoodEntry)

    @Query("DELETE FROM foodEntry WHERE foodEntryId = :id")
    suspend fun deleteById(id: Int)
}