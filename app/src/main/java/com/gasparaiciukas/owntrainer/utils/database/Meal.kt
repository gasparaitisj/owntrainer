package com.gasparaiciukas.owntrainer.utils.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "meal")
data class Meal(
    @PrimaryKey(autoGenerate = true) var mealId: Int = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "instructions") var instructions: String,
) {
    @ColumnInfo(name = "calories")
    var calories: Double = 0.0

    @ColumnInfo(name = "carbs")
    var carbs: Double = 0.0

    @ColumnInfo(name = "fat")
    var fat: Double = 0.0

    @ColumnInfo(name = "protein")
    var protein: Double = 0.0
}
