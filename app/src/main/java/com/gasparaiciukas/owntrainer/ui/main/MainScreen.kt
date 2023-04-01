package com.gasparaiciukas.owntrainer.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.gasparaiciukas.owntrainer.ui.home.HomeScreen
import com.gasparaiciukas.owntrainer.ui.meals.MealsScreen
import com.gasparaiciukas.owntrainer.ui.progress.ProgressScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavigationItem(
            label = "Home",
            icon = Icons.Filled.Home,
        ),
        BottomNavigationItem(
            label = "Meals",
            icon = Icons.Filled.RestaurantMenu,
        ),
        BottomNavigationItem(
            label = "Progress",
            icon = Icons.Filled.AutoGraph,
        ),
    )
    Scaffold(
        topBar = { TopAppBar(title = { Text(items[selectedItem].label) }) },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> MealsScreen()
                2 -> ProgressScreen()
            }
        }
    }
}

data class BottomNavigationItem(
    val label: String,
    val icon: ImageVector,
)
