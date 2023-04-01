package com.gasparaiciukas.owntrainer.model

import androidx.annotation.StringRes
import com.gasparaiciukas.owntrainer.R

enum class NutrientType(
    @StringRes val title: Int
) {
    ENERGY(R.string.calories),
    PROTEIN(R.string.protein),
    CARBS(R.string.carbs),
    FAT(R.string.fat),
}
