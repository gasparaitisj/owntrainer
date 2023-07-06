package com.gasparaiciukas.owntrainer.ui.meals.meal.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.ui.main.Screen
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val mealId: Int = checkNotNull(savedStateHandle[Screen.MealDetails.KEY_MEAL_ID])
    val meal: MutableStateFlow<MealWithFoodEntries?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            meal.value = diaryRepository.getMealWithFoodEntriesById(mealId)
        }
    }
}
