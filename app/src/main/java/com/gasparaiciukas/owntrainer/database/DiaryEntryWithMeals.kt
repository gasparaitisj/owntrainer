package com.gasparaiciukas.owntrainer.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DiaryEntryWithMeals(
    @Embedded val diaryEntry: DiaryEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(DiaryEntryMealCrossRef::class)
    )
    val meals: List<Meal>
)