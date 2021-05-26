package com.gasparaiciukas.owntrainer.database

import io.realm.RealmObject

open class Food : RealmObject() {
    var title: String = ""
    var caloriesPer100G: Double = 0.0
    var carbsPer100G: Double = 0.0
    var fatPer100G: Double = 0.0
    var proteinPer100G: Double = 0.0
    var calories: Double = 0.0
    var carbs: Double = 0.0
    var fat: Double = 0.0
    var protein: Double = 0.0
    var quantityInG: Double = 0.0

    fun calculateCarbs(carbsPer100G: Double, quantity: Double): Double {
        return carbsPer100G / 100 * quantity
    }

    fun calculateCalories(caloriesPer100G: Double, quantity: Double): Double {
        return caloriesPer100G / 100 * quantity
    }

    fun calculateFat(fatPer100G: Double, quantity: Double): Double {
        return fatPer100G / 100 * quantity
    }

    fun calculateProtein(proteinPer100G: Double, quantity: Double): Double {
        return proteinPer100G / 100 * quantity
    }
}