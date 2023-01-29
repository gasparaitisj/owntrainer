package com.gasparaiciukas.owntrainer.utils.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diaryEntryMeal",
    indices = [Index("diaryEntryId"), Index("mealId")]
)
data class DiaryEntryMealCrossRef(
    val diaryEntryId: Int,
    val mealId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
