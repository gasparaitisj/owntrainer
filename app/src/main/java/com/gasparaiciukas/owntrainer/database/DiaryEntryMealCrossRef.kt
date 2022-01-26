package com.gasparaiciukas.owntrainer.database

import androidx.room.Entity

@Entity(primaryKeys = ["diaryEntryId", "mealId"])
data class DiaryEntryMealCrossRef (
    val diaryEntryId: Int,
    val mealId: Int
)