package com.gasparaiciukas.owntrainer.utils.database

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
    val calories get() = calculateCalories()
    val carbs get() = calculateCarbs()
    val fat get() = calculateFat()
    val protein get() = calculateProtein()

    private fun calculateCalories(): Double {
        var mCalories = 0.0
        for (f in foodEntries) {
            mCalories += f.calories
        }
        return mCalories
    }

    private fun calculateCarbs(): Double {
        var mCarbs = 0.0
        for (f in foodEntries) {
            mCarbs += f.carbs
        }
        return mCarbs
    }

    private fun calculateFat(): Double {
        var mFat = 0.0
        for (f in foodEntries) {
            mFat += f.fat
        }
        return mFat
    }

    private fun calculateProtein(): Double {
        var mProtein = 0.0
        for (f in foodEntries) {
            mProtein += f.protein
        }
        return mProtein
    }
}
