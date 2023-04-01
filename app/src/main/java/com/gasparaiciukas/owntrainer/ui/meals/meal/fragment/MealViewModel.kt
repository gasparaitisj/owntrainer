package com.gasparaiciukas.owntrainer.ui.meals.meal.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.utils.database.Meal
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MealViewModel @Inject internal constructor(
    val diaryRepository: DiaryRepository,
) : ViewModel() {

    val meals = diaryRepository.getAllMealsWithFoodEntries()

    var title = ""
    var instructions = ""
    var isTitleCorrect = false
    var isInstructionsCorrect = false

    fun deleteMeal(mealId: Int) {
        viewModelScope.launch {
            diaryRepository.deleteMealById(mealId)
        }
    }

    fun createMeal(title: String, instructions: String) {
        viewModelScope.launch {
            val meal = Meal(
                title = title,
                instructions = instructions,
            )
            diaryRepository.insertMeal(meal)
        }
    }
}
