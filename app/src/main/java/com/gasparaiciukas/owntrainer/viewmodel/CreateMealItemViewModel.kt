package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.DiaryEntryRepository
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealRepository
import com.gasparaiciukas.owntrainer.database.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMealItemViewModel @Inject internal constructor(
    private val mealRepository: MealRepository
) : ViewModel() {
    fun addMealToDatabase(title: String, instructions: String) {
        viewModelScope.launch {
            val meal = Meal(
                parseTitle(title),
                parseInstructions(instructions)
            )
            mealRepository.addMeal(meal)
        }
    }

    private fun parseTitle(title: String) : String {
        return when {
            title.trim { it <= ' ' }
                .isEmpty() -> "No title"
            else -> title
        }
    }

    private fun parseInstructions(instructions: String) : String {
        return when {
            instructions.trim { it <= ' ' }
                .isEmpty() -> "No instructions"
            else -> instructions
        }
    }
}