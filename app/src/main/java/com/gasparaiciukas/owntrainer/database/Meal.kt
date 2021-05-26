package com.gasparaiciukas.owntrainer.database

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class Meal : RealmObject() {
    @PrimaryKey @Required var title: String? = null
    var foodList = RealmList<Food>()
    var calories: Double = 0.0
    var carbs: Double = 0.0
    var fat: Double = 0.0
    var protein: Double = 0.0
    var instructions: String = ""

    fun calculateCalories(): Double {
        var mCalories = 0.0
        for (f in foodList) {
            mCalories += f.calories
        }
        return mCalories
    }

    fun calculateCarbs(): Double {
        var mCarbs = 0.0
        for (f in foodList) {
            mCarbs += f.carbs
        }
        return mCarbs
    }

    fun calculateFat(): Double {
        var mFat = 0.0
        for (f in foodList) {
            mFat += f.fat
        }
        return mFat
    }

    fun calculateProtein(): Double {
        var mProtein = 0.0
        for (f in foodList) {
            mProtein += f.protein
        }
        return mProtein
    }
}