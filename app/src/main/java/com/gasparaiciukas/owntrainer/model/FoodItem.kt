package com.gasparaiciukas.owntrainer.model

data class FoodItem(
    val title: String = "",
    val quantity: Double = 100.0,
    var calories: Double = 0.0,
    var protein: Double = 0.0,
    var fat: Double = 0.0,
    var carbs: Double = 0.0
)
