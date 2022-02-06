package com.gasparaiciukas.owntrainer.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DiaryEntryWithMeals(
    @Embedded val diaryEntry: DiaryEntry,
    @Relation(
        parentColumn = "diaryEntryId",
        entityColumn = "mealId",
        associateBy = Junction(DiaryEntryMealCrossRef::class)
    )
    val meals: List<Meal>
) {
    fun calculateTotalCalories(): Double {
        var mCalories = 0.0
        for (m in meals) {
            mCalories += m.calories
        }
        return mCalories
    }

    fun calculateTotalProtein(): Double {
        var mProtein = 0.0
        for (m in meals) {
            mProtein += m.protein
        }
        return mProtein
    }

    fun calculateTotalFat(): Double {
        var mFat = 0.0
        for (m in meals) {
            mFat += m.fat
        }
        return mFat
    }

    fun calculateTotalCarbs(): Double {
        var mCarbs = 0.0
        for (m in meals) {
            mCarbs += m.carbs
        }
        return mCarbs
    }
}