package com.gasparaiciukas.owntrainer.ui.meals.meal

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.utils.database.Meal
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class MealViewModel @Inject constructor(
    val diaryRepository: DiaryRepository,
) : ViewModel() {
    val meals: MutableStateFlow<List<MealWithFoodEntries>> = MutableStateFlow(mutableListOf())
    private val localMeals: Flow<List<MealWithFoodEntries>> = diaryRepository.getAllMealsWithFoodEntries()
    private var lastLocalMeals = listOf<MealWithFoodEntries>()

    private val _searchText = MutableStateFlow(TextFieldValue(""))
    val searchText: StateFlow<TextFieldValue> = _searchText.asStateFlow()

    init {
        viewModelScope.launch {
            searchText
                .debounce(DEBOUNCE_TIMEOUT_MILLIS)
                .collect { textFieldValue ->
                    updateByQuery(textFieldValue.text)
                }
        }
        viewModelScope.launch {
            localMeals.collectLatest { list: List<MealWithFoodEntries> ->
                lastLocalMeals = list
                updateByQuery(_searchText.value.text)
            }
        }
    }

    private fun updateByQuery(query: String) = meals.update {
        if (query.isNotBlank()) {
            lastLocalMeals.filter { meal ->
                meal.meal.title.contains(query, ignoreCase = true)
            }
        } else {
            lastLocalMeals
        }
    }

    fun onQueryChanged(text: String) = _searchText.update { it.copy(text = text) }

    fun createMeal(title: String, instructions: String) {
        viewModelScope.launch {
            val meal = Meal(
                title = title,
                instructions = instructions,
            )
            diaryRepository.insertMeal(meal)
        }
    }

    fun deleteMeal(mealId: Int) {
        viewModelScope.launch {
            diaryRepository.deleteMealById(mealId)
        }
    }

    companion object {
        private const val DEBOUNCE_TIMEOUT_MILLIS = 1000L
    }
}
