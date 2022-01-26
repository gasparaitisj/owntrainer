package com.gasparaiciukas.owntrainer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal")
    fun getAll(): Flow<List<Meal>>

    @Insert
    suspend fun insertAll(vararg meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)
}