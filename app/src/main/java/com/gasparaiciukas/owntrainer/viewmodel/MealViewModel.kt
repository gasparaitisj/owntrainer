package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.repository.DefaultDiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject internal constructor(
    private val diaryRepository: DefaultDiaryRepository
) : ViewModel() {
    val ldMeals = diaryRepository.getMealsWithFoodEntries().asLiveData()
    lateinit var meals: List<MealWithFoodEntries>

    suspend fun deleteMealFromMeals(mealId: Int) = diaryRepository.deleteMealById(mealId)
}