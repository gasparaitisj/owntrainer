package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.*
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddMealToDiaryViewModel @Inject internal constructor(
    private val mealRepository: MealRepository,
    private val diaryEntryRepository: DiaryEntryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val diaryEntryId: Int? = savedStateHandle["primaryKey"]

    val ldMeals = mealRepository.getMeals().asLiveData()
    lateinit var meals: List<Meal>

    suspend fun addMealToDiary(meal: Meal) {
        if (diaryEntryId != null) {
            diaryEntryRepository.insertDiaryEntryMealCrossRef(
                DiaryEntryMealCrossRef(
                    diaryEntryId,
                    meal.mealId
                )
            )
        } else {
            Timber.e("Adding meal to diary failed! diaryEntryId is null.")
        }
    }
}