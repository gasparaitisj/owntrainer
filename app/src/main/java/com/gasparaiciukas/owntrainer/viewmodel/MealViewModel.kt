package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.MealRepository
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject internal constructor(
    private val mealRepository: MealRepository
) : ViewModel() {
    val ldMeals = mealRepository.getMealsWithFoodEntries().asLiveData()
    lateinit var meals: List<MealWithFoodEntries>

    suspend fun deleteMealFromMeals(mealId: Int) = mealRepository.deleteMealById(mealId)
}