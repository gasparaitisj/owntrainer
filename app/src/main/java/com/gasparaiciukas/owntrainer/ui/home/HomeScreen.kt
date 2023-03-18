package com.gasparaiciukas.owntrainer.ui.home

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.utils.database.Meal

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column {
        NavigationRow()
        StatisticsColumn()
        MealsColumn(viewModel.meals)
    }
}

@Composable
fun NavigationRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                // Do nothing.
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = "Back arrow"
            )
        }
        Text(
            text = "Sample Text",
            style = MaterialTheme.typography.headlineSmall
        )
        IconButton(
            onClick = {
                // Do nothing.
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "Forward arrow"
            )
        }
    }
}

@Composable
fun StatisticsColumn() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        HomeStatisticsRow(
            modifier = Modifier.padding(bottom = 8.dp),
            item = StatisticsItem(
                type = NutrientType.ENERGY,
                title = "Calories",
                percentage = "50",
                value = "500"
            )
        )
        HomeStatisticsRow(
            modifier = Modifier.padding(bottom = 8.dp),
            item = StatisticsItem(
                type = NutrientType.PROTEIN,
                title = "Protein",
                percentage = "50",
                value = "500"
            )
        )
        HomeStatisticsRow(
            modifier = Modifier.padding(bottom = 8.dp),
            item = StatisticsItem(
                type = NutrientType.CARBS,
                title = "Carbs",
                percentage = "50",
                value = "500"
            )
        )
        HomeStatisticsRow(
            item = StatisticsItem(
                type = NutrientType.FAT,
                title = "Fat",
                percentage = "50",
                value = "500"
            )
        )
    }
}

@Composable
private fun HomeStatisticsRow(
    modifier: Modifier = Modifier,
    item: StatisticsItem
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.weight(2f),
            text = item.title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.append_percent_sign, item.percentage),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.weight(2f),
            text = if (item.type == NutrientType.ENERGY) {
                stringResource(R.string.append_kcal, item.value)
            } else {
                stringResource(R.string.append_g, item.value)
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MealsColumn(
    meals: List<Meal>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = meals.count(),
            key = { meals[it].mealId }
        ) { index ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = meals[index].title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.append_kcal, meals[index].calories),
                    style = MaterialTheme.typography.labelMedium
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
    NavigationRow()
}

@Composable
@Preview(showBackground = true)
fun HomeStatisticsPreview() {
    StatisticsColumn()
}
