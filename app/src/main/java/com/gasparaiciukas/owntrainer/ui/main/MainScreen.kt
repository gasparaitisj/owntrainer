package com.gasparaiciukas.owntrainer.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.ui.home.HomeScreen
import com.gasparaiciukas.owntrainer.ui.meals.MealsScreen
import com.gasparaiciukas.owntrainer.ui.meals.meal.details.MealDetailsScreen
import com.gasparaiciukas.owntrainer.ui.progress.ProgressScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val title = bottomNavigationItems.find { item ->
        item.first == navBackStackEntry?.destination?.route
    }?.second?.let { id -> stringResource(id) }.orEmpty()
    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
        bottomBar = { MainBottomNavigation(navController) },
    ) { paddingValues ->
        MainNavigation(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startingScreen = Screen.Home,
        )
    }
}

@Composable
private fun MainBottomNavigation(
    navController: NavHostController,
) {
    NavigationBar {
        bottomNavigationItems.forEach { item: Triple<String, Int, ImageVector> ->
            val isSelected = navController.currentDestination?.hierarchy?.any { it.route == item.first } == true
            NavigationBarItem(
                icon = { Icon(item.third, stringResource(id = item.second)) },
                label = { Text(stringResource(item.second)) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.first) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}

@Composable
private fun MainNavigation(
    modifier: Modifier,
    navController: NavHostController,
    startingScreen: Screen,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startingScreen.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Meals.route) {
            MealsScreen(
                onNavigateToMealDetails = { mealId ->
                    navController.navigate(
                        Screen.MealDetails.destination(mealId),
                    )
                },
            )
        }
        composable(Screen.Progress.route) {
            ProgressScreen()
        }
        composable(
            route = Screen.MealDetails.route,
            arguments = listOf(
                navArgument(Screen.MealDetails.KEY_MEAL_ID) {
                    type = NavType.IntType
                },
            ),
        ) {
            MealDetailsScreen()
        }
    }
}

private val bottomNavigationItems: List<Triple<String, Int, ImageVector>> = listOf(
    Triple(Screen.Home.route, R.string.home, Icons.Filled.Home),
    Triple(Screen.Meals.route, R.string.meals, Icons.Filled.RestaurantMenu),
    Triple(Screen.Progress.route, R.string.progress, Icons.Filled.AutoGraph),
)
