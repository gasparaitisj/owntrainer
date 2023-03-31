package com.gasparaiciukas.owntrainer.ui.home

import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryWithMeals
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.database.User

data class HomeUiState(
    val meals: List<MealWithFoodEntries>,
    val user: User,
    val diaryEntryWithMeals: DiaryEntryWithMeals,
    val proteinConsumed: Double,
    val fatConsumed: Double,
    val carbsConsumed: Double,
    val caloriesConsumed: Double,
    val caloriesPercentage: Double,
    val proteinPercentage: Double,
    val fatPercentage: Double,
    val carbsPercentage: Double,
)
