package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject internal constructor(
    val diaryRepository: DiaryRepository
) : ViewModel() {
    val ldMeals = diaryRepository.getAllMealsWithFoodEntries().asLiveData()

    fun deleteMeal(mealId: Int) {
        viewModelScope.launch {
            diaryRepository.deleteMealById(mealId)
        }
    }
}