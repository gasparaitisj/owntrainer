package com.gasparaiciukas.owntrainer.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodEntryParcelable(
    var title: String = "",
    var caloriesPer100G: Double = 0.0,
    var carbsPer100G: Double = 0.0,
    var fatPer100G: Double = 0.0,
    var proteinPer100G: Double = 0.0,
    var calories: Double = 0.0,
    var carbs: Double = 0.0,
    var fat: Double = 0.0,
    var protein: Double = 0.0,
    var quantityInG: Double = 0.0
) : Parcelable