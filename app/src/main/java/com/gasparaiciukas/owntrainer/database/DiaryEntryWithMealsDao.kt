package com.gasparaiciukas.owntrainer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryWithMealsDao {
    @Transaction
    @Query("SELECT * FROM diaryEntry WHERE year = :year AND dayOfYear = :dayOfYear")
    fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef)
}