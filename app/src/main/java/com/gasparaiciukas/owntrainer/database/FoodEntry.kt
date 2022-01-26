package com.gasparaiciukas.owntrainer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "foodEntry")
data class FoodEntry(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "caloriesPer100G") var caloriesPer100G: Double,
    @ColumnInfo(name = "carbsPer100G") var carbsPer100G: Double,
    @ColumnInfo(name = "fatPer100G") var fatPer100G: Double,
    @ColumnInfo(name = "proteinPer100G") var proteinPer100G: Double,
    @ColumnInfo(name = "quantityInG") var quantityInG: Double
) {
    @PrimaryKey(autoGenerate = true) var foodEntryId: Int = 0
    @ColumnInfo(name = "calories") var calories: Double = 0.0
    @ColumnInfo(name = "carbs") var carbs: Double = 0.0
    @ColumnInfo(name = "fat") var fat: Double = 0.0
    @ColumnInfo(name = "protein") var protein: Double = 0.0

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