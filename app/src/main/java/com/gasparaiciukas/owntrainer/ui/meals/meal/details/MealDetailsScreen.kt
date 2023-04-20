package com.gasparaiciukas.owntrainer.ui.meals.meal.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    viewModel: MealDetailsViewModel = hiltViewModel(),
) {
    val meal by viewModel.meal.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Meal details") }) },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            meal?.let { mealWithFoodEntries ->
                mealWithFoodEntries
            }
        }
    }
}
