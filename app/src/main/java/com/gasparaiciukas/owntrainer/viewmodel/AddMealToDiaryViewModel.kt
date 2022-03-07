package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.DiaryEntryMealCrossRef
import com.gasparaiciukas.owntrainer.repository.DefaultDiaryRepository
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddMealToDiaryViewModel @Inject internal constructor(
    private val diaryRepository: DefaultDiaryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val diaryEntryId: Int? = savedStateHandle["primaryKey"]

    val ldMeals = diaryRepository.getMealsWithFoodEntries().asLiveData()
    lateinit var meals: List<MealWithFoodEntries>

    suspend fun addMealToDiary(mealWithFoodEntries: MealWithFoodEntries) {
        if (diaryEntryId != null) {
            diaryRepository.insertDiaryEntryMealCrossRef(
                DiaryEntryMealCrossRef(
                    diaryEntryId,
                    mealWithFoodEntries.meal.mealId
                )
            )
        } else {
            Timber.e("Adding meal to diary failed! diaryEntryId is null.")
        }
    }
}