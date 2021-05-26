package com.gasparaiciukas.owntrainer.database

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class DiaryEntry : RealmObject() {
    @PrimaryKey
    @Required
    var yearAndDayOfYear: String? = null
    var year: Int = 0
    var dayOfYear: Int = 0
    var dayOfWeek: Int = 0
    var monthOfYear: Int = 0
    var dayOfMonth: Int = 0
    var meals = RealmList<Meal>()

    fun calculateTotalCalories(meals: List<Meal>): Double {
        var mCalories = 0.0
        for (m in meals) {
            mCalories += m.calculateCalories()
        }
        return mCalories
    }

    fun calculateTotalProtein(meals: List<Meal>): Double {
        var mProtein = 0.0
        for (m in meals) {
            mProtein += m.calculateProtein()
        }
        return mProtein
    }

    fun calculateTotalFat(meals: List<Meal>): Double {
        var mFat = 0.0
        for (m in meals) {
            mFat += m.calculateFat()
        }
        return mFat
    }

    fun calculateTotalCarbs(meals: List<Meal>): Double {
        var mCarbs = 0.0
        for (m in meals) {
            mCarbs += m.calculateCarbs()
        }
        return mCarbs
    }
}