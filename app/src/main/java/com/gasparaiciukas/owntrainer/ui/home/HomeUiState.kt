package com.gasparaiciukas.owntrainer.ui.home

import com.gasparaiciukas.owntrainer.model.StatisticsItem
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryWithMeals
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.database.User

data class HomeUiState(
    val allMeals: List<MealWithFoodEntries>,
    val meals: List<MealWithFoodEntries>,
    val user: User,
    val diaryEntryWithMeals: DiaryEntryWithMeals,
    val proteinItem: StatisticsItem,
    val carbsItem: StatisticsItem,
    val fatItem: StatisticsItem,
    val energyItem: StatisticsItem,
)
