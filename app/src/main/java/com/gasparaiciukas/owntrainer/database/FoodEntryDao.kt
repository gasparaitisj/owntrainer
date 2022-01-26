package com.gasparaiciukas.owntrainer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM foodEntry")
    fun getAll(): List<FoodEntry>

    @Insert
    fun insertAll(vararg foodEntry: FoodEntry)

    @Delete
    fun delete(foodEntry: FoodEntry)

}