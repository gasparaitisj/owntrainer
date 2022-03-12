package com.gasparaiciukas.owntrainer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foodEntry")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) var foodEntryId: Int = 0,
    @ColumnInfo(name = "mealId") var mealId: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "caloriesPer100G") var caloriesPer100G: Double,
    @ColumnInfo(name = "carbsPer100G") var carbsPer100G: Double,
    @ColumnInfo(name = "fatPer100G") var fatPer100G: Double,
    @ColumnInfo(name = "proteinPer100G") var proteinPer100G: Double,
    @ColumnInfo(name = "quantityInG") var quantityInG: Double,
) {
    val calories: Double get() =  caloriesPer100G / 100 * quantityInG
    val carbs: Double get() = carbsPer100G / 100 * quantityInG
    val fat: Double get() = fatPer100G / 100 * quantityInG
    val protein: Double get() = proteinPer100G / 100 * quantityInG
}