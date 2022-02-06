package com.gasparaiciukas.owntrainer.database

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithFoodEntries(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "mealId",
    )
    var foodEntries: List<FoodEntry>
) {
    fun calculateCalories(): Double {
        var mCalories = 0.0
        for (f in foodEntries) {
            mCalories += f.calories
        }
        return mCalories
    }

    fun calculateCarbs(): Double {
        var mCarbs = 0.0
        for (f in foodEntries) {
            mCarbs += f.carbs
        }
        return mCarbs
    }

    fun calculateFat(): Double {
        var mFat = 0.0
        for (f in foodEntries) {
            mFat += f.fat
        }
        return mFat
    }

    fun calculateProtein(): Double {
        var mProtein = 0.0
        for (f in foodEntries) {
            mProtein += f.protein
        }
        return mProtein
    }
}