package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.DiaryEntryMealCrossRef
import com.gasparaiciukas.owntrainer.database.DiaryEntryRepository
import com.gasparaiciukas.owntrainer.database.MealRepository
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddMealToDiaryViewModel @Inject internal constructor(
    private val mealRepository: MealRepository,
    private val diaryEntryRepository: DiaryEntryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val diaryEntryId: Int? = savedStateHandle["primaryKey"]

    val ldMeals = mealRepository.getMealsWithFoodEntries().asLiveData()
    lateinit var meals: List<MealWithFoodEntries>

    suspend fun addMealToDiary(mealWithFoodEntries: MealWithFoodEntries) {
        if (diaryEntryId != null) {
            diaryEntryRepository.insertDiaryEntryMealCrossRef(
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