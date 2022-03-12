package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMealItemViewModel @Inject internal constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    fun createMeal(title: String, instructions: String) {
        viewModelScope.launch {
            val meal = Meal(
                title = parseTitle(title),
                instructions = parseInstructions(instructions)
            )
            diaryRepository.insertMeal(meal)
        }
    }

    private fun parseTitle(title: String): String {
        return when {
            title.trim { it <= ' ' }
                .isEmpty() -> "No title"
            else -> title
        }
    }

    private fun parseInstructions(instructions: String): String {
        return when {
            instructions.trim { it <= ' ' }
                .isEmpty() -> "No instructions"
            else -> instructions
        }
    }
}