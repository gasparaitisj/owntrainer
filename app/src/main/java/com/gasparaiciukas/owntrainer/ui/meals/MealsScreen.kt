package com.gasparaiciukas.owntrainer.ui.meals

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.gasparaiciukas.owntrainer.ui.meals.food.FoodScreen
import com.gasparaiciukas.owntrainer.ui.meals.meal.MealScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealsScreen(
    onNavigateToMealDetails: (Int) -> Unit,
) {
    val tabs = listOf("Foods", "Meals")
    val pagerState = rememberPagerState(initialPage = 0)
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            pageCount = tabs.size,
            beyondBoundsPageCount = tabs.size,
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                when (page) {
                    0 -> FoodScreen()
                    1 -> MealScreen(
                        onNavigateToMealDetails = onNavigateToMealDetails,
                    )
                }
            }
        }
    }
}
