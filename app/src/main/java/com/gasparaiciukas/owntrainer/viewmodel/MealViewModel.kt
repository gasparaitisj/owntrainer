package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject internal constructor(
    val diaryRepository: DiaryRepository
) : ViewModel() {
    val ldMeals = diaryRepository.getAllMealsWithFoodEntries().asLiveData()
    lateinit var meals: List<MealWithFoodEntries>

    suspend fun deleteMeal(mealId: Int) = diaryRepository.deleteMealById(mealId)
}