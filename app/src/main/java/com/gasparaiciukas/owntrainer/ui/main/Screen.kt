package com.gasparaiciukas.owntrainer.ui.main

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Meals : Screen("meals")
    object Progress : Screen("progress")
    object MealDetails : Screen("mealdetails/{mealId}") {
        fun destination(mealId: Int) = route.replace(
            oldValue = "{$KEY_MEAL_ID}",
            newValue = mealId.toString(),
        )
        const val KEY_MEAL_ID = "mealId"
    }
}
