package com.gasparaiciukas.owntrainer.ui.meals.food

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.model.FoodItem
import com.gasparaiciukas.owntrainer.utils.compose.CustomTextField

@Composable
fun FoodScreen(
    viewModel: FoodViewModel = hiltViewModel(),
) {
    val foods by viewModel.foods.collectAsState()
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val lazyListState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -9) >=
                (lazyListState.layoutInfo.totalItemsCount - 6)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        FoodTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(36.dp)
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally),
            textState = textState,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClicked = viewModel::onQueryChanged,
        )
        FoodLazyColumn(
            lazyListState = lazyListState,
            foods = foods,
        )
    }

    LaunchedEffect(key1 = shouldStartPaginate.value) {
        if (shouldStartPaginate.value) {
            viewModel.getFoods(textState.value.text)
        }
    }
}

@Composable
private fun FoodTextField(
    modifier: Modifier,
    textState: MutableState<TextFieldValue>,
    onQueryChanged: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    CustomTextField(
        modifier = modifier,
        value = textState.value.text,
        onValueChange = { value ->
            textState.value = TextFieldValue(value)
            onQueryChanged(value)
        },
        placeholderText = {
            Text(
                text = "Food search",
                color = Color.Gray.copy(alpha = 0.3f),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { onSearchClicked(textState.value.text) },
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
private fun FoodLazyColumn(
    lazyListState: LazyListState,
    foods: List<FoodItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
    ) {
        items(
            count = foods.count(),
        ) { index ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                FoodItemRow(foods[index])
            }
        }
    }
}

@Composable
private fun FoodItemRow(
    food: FoodItem,
) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = food.title,
        style = MaterialTheme.typography.bodyLarge,
    )
    Row {
        Text(
            text = stringResource(R.string.append_g, food.quantity),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = stringResource(R.string.append_kcal, food.calories),
            style = MaterialTheme.typography.labelMedium,
        )
    }
    Row {
        Text(
            text = stringResource(R.string.append_g, food.protein),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = stringResource(R.string.append_g, food.carbs),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = stringResource(R.string.append_g, food.fat),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
