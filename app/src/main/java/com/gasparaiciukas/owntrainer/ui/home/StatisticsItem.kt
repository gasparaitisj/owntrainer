package com.gasparaiciukas.owntrainer.ui.home

data class StatisticsItem(
    val type: NutrientType = NutrientType.ENERGY,
    val title: String = "",
    val percentage: String = "",
    val value: String = ""
)
