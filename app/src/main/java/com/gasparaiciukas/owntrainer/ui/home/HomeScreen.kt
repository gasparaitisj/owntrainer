package com.gasparaiciukas.owntrainer.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.model.NutrientType
import com.gasparaiciukas.owntrainer.model.StatisticsItem
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.network.Status

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    when (uiState.value?.status) {
        Status.SUCCESS -> {
            uiState.value?.data?.let { state ->
                Box {
                    Column {
                        NavigationRow(
                            displayDate = stringResource(
                                R.string.date_navigation_formatted,
                                DateFormatter.dayOfWeekToString(state.user.dayOfWeek, context),
                                DateFormatter.monthOfYearToString(state.user.month, context),
                                state.user.dayOfMonth.toString(),
                            ),
                            onNavigateBack = viewModel::updateUserToPreviousDay,
                            onNavigateCurrent = viewModel::updateUserToCurrentDay,
                            onNavigateForward = viewModel::updateUserToNextDay,
                        )
                        StatisticsColumn(
                            energyItem = state.energyItem,
                            proteinItem = state.proteinItem,
                            carbsItem = state.carbsItem,
                            fatItem = state.fatItem,
                        )
                        MealsColumn(
                            meals = uiState.value?.data?.meals ?: listOf(),
                        )
                    }
                }
            }
        }
        Status.LOADING -> {
        }
        Status.ERROR, null -> {
        }
    }
}

@Composable
fun NavigationRow(
    displayDate: String,
    onNavigateBack: () -> Unit,
    onNavigateCurrent: () -> Unit,
    onNavigateForward: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = onNavigateBack,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = "Back arrow",
            )
        }
        Text(
            modifier = Modifier.clickable(onClick = onNavigateCurrent),
            text = displayDate,
            style = MaterialTheme.typography.headlineSmall,
        )
        IconButton(
            onClick = onNavigateForward,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "Forward arrow",
            )
        }
    }
}

@Composable
fun StatisticsColumn(
    energyItem: StatisticsItem,
    proteinItem: StatisticsItem,
    carbsItem: StatisticsItem,
    fatItem: StatisticsItem,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
    ) {
        HomeStatisticsRow(
            modifier = Modifier.padding(bottom = 8.dp),
            item = energyItem,
        )
        HomeStatisticsRow(
            modifier = Modifier.padding(bottom = 8.dp),
            item = proteinItem,
        )
        HomeStatisticsRow(
            modifier = Modifier.padding(bottom = 8.dp),
            item = carbsItem,
        )
        HomeStatisticsRow(
            item = fatItem,
        )
    }
}

@Composable
private fun HomeStatisticsRow(
    modifier: Modifier = Modifier,
    item: StatisticsItem,
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.weight(2f),
            text = stringResource(item.title),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.append_percent_sign, item.percentage),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            modifier = Modifier.weight(2f),
            text = if (item.type == NutrientType.ENERGY) {
                stringResource(R.string.append_kcal, item.value)
            } else {
                stringResource(R.string.append_g, item.value)
            },
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun MealsColumn(
    meals: List<MealWithFoodEntries>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            count = meals.count(),
            key = { meals[it].meal.mealId },
        ) { index ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = meals[index].meal.title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.append_kcal, meals[index].calories),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
@Preview(showBackground = true)
fun HomeNavigationPreview() {
    NavigationRow(
        displayDate = "",
        onNavigateBack = {},
        onNavigateCurrent = {},
        onNavigateForward = {},
    )
}

@Composable
@Preview(showBackground = true)
fun HomeStatisticsPreview() {
    val statisticsItem = StatisticsItem(
        type = NutrientType.ENERGY,
        title = R.string.calories,
        percentage = "50",
        value = "500",
    )
    StatisticsColumn(
        statisticsItem,
        statisticsItem,
        statisticsItem,
        statisticsItem,
    )
}
