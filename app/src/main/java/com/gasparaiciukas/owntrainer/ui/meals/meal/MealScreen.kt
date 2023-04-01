package com.gasparaiciukas.owntrainer.ui.meals.meal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MealScreen(
    viewModel: MealViewModel = hiltViewModel(),
) {
    val meals = viewModel.meals.collectAsState(initial = listOf())

}
