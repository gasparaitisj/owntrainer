package com.gasparaiciukas.owntrainer.model

import androidx.annotation.StringRes
import com.gasparaiciukas.owntrainer.R

data class StatisticsItem(
    val type: NutrientType = NutrientType.ENERGY,
    @StringRes val title: Int = R.string.calories,
    val percentage: String = "",
    val value: String = ""
)
