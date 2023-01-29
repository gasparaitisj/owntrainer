package com.gasparaiciukas.owntrainer.utils.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryWithMealsDao {
    @Transaction
    @Query("SELECT * FROM diaryEntry WHERE year = :year AND dayOfYear = :dayOfYear")
    fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef)

    @Query("DELETE FROM diaryEntryMeal WHERE id IN (SELECT id FROM diaryEntryMeal WHERE diaryEntryId == :diaryEntryId AND mealId == :mealId LIMIT 1)")
    suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int)
}
