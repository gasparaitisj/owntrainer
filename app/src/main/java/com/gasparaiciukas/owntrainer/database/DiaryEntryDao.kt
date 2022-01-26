package com.gasparaiciukas.owntrainer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM diaryEntry")
    fun getAll(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diaryEntry WHERE year = :year AND dayOfYear = :dayOfYear")
    fun getDiaryEntryByYearAndDayOfYear(year: Int, dayOfYear: Int): Flow<DiaryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg diaryEntry: DiaryEntry)

    @Delete
    suspend fun delete(diaryEntry: DiaryEntry)
}