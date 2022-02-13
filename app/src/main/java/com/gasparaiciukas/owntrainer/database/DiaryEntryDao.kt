package com.gasparaiciukas.owntrainer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM diaryEntry")
    fun getAllDiaryEntries(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diaryEntry WHERE year = :year AND dayOfYear = :dayOfYear")
    fun getDiaryEntryByYearAndDayOfYear(year: Int, dayOfYear: Int): Flow<DiaryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(diaryEntry: DiaryEntry)

    @Query("DELETE FROM diaryEntry WHERE year = :year AND dayOfYear = :dayOfYear")
    suspend fun deleteDiaryEntryByYearAndDayOfYear(year: Int, dayOfYear: Int)
}