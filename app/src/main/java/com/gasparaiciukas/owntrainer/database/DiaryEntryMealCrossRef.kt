package com.gasparaiciukas.owntrainer.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diaryEntryMeal")
data class DiaryEntryMealCrossRef (
    val diaryEntryId: Int,
    val mealId: Int
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}