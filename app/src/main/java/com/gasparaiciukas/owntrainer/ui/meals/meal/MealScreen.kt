package com.gasparaiciukas.owntrainer.ui.meals.meal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.utils.compose.CustomTextField
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries

@Composable
fun MealScreen(
    onNavigateToMealDetails: (Int) -> Unit,
    viewModel: MealViewModel = hiltViewModel(),
) {
    val meals by viewModel.meals.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MealTextField(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(36.dp)
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally),
                searchText = searchText,
                onQueryChanged = viewModel::onQueryChanged,
                onSearchClicked = viewModel::onQueryChanged,
            )
            MealLazyColumn(
                meals = meals,
                onItemClicked = onNavigateToMealDetails,
            )
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp),
            onClick = { viewModel.createMeal("New meal", "") },
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add meal",
            )
            Text(text = "Add meal")
        }
    }
}

@Composable
private fun MealTextField(
    modifier: Modifier,
    searchText: TextFieldValue,
    onQueryChanged: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    CustomTextField(
        modifier = modifier,
        value = searchText.text,
        onValueChange = onQueryChanged,
        placeholderText = {
            Text(
                text = "Meal search",
                color = Color.Gray.copy(alpha = 0.3f),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { onSearchClicked(searchText.text) },
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                )
            }
        },
    )
}

@Composable
private fun MealLazyColumn(
    meals: List<MealWithFoodEntries>,
    onItemClicked: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            count = meals.count(),
        ) { index ->
            MealItemRow(
                mealWithFoodEntries = meals[index],
                onItemClicked = onItemClicked,
            )
        }
    }
}

@Composable
private fun MealItemRow(
    mealWithFoodEntries: MealWithFoodEntries,
    onItemClicked: (Int) -> Unit,
) {
    val meal = mealWithFoodEntries.meal
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClicked(meal.mealId) },
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = meal.title,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row {
            Text(
                text = stringResource(R.string.append_kcal, meal.calories),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Row {
            Text(
                text = stringResource(R.string.append_g, meal.protein),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = stringResource(R.string.append_g, meal.carbs),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = stringResource(R.string.append_g, meal.fat),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
fun CreateMealDialog(
    showDialog: MutableState<Boolean>,
) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = {
            Text(text = "Create meal")
        },
        text = {
            Text(text = "Some text")
        },
        confirmButton = {
            Button(
                onClick = {
                    showDialog.value = false
                },
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(
                onClick = { showDialog.value = false },
            ) {
                Text("Cancel")
            }
        },
    )
}
