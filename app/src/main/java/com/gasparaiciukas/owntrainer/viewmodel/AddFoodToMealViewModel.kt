package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.utils.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFoodToMealViewModel @Inject constructor(
    val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val foodItem: Food? = savedStateHandle["foodItem"]
    private val quantity: Int? = savedStateHandle["quantity"]

    val ldMeals = diaryRepository.getAllMealsWithFoodEntries().asLiveData()

    fun addFoodToMeal(mealWithFoodEntries: MealWithFoodEntries) {
        viewModelScope.launch {
            safeLet(quantity, foodItem) { quantity, foodItem ->
                var protein = 0.0
                var fat = 0.0
                var carbs = 0.0
                var calories = 0.0

                val nutrients = foodItem.foodNutrients
                if (nutrients != null) {
                    for (nutrient in nutrients) {
                        if (nutrient.nutrientId == 1003) protein = (nutrient.value ?: 0.0)
                        if (nutrient.nutrientId == 1004) fat = (nutrient.value ?: 0.0)
                        if (nutrient.nutrientId == 1005) carbs = (nutrient.value ?: 0.0)
                        if (nutrient.nutrientId == 1008) calories = (nutrient.value ?: 0.0)
                    }
                }
                val foodEntry = FoodEntry(
                    mealId = mealWithFoodEntries.meal.mealId,
                    title = foodItem.description.toString(),
                    caloriesPer100G = calories,
                    carbsPer100G = carbs,
                    fatPer100G = fat,
                    proteinPer100G = protein,
                    quantityInG = quantity.toDouble()
                )
                diaryRepository.insertFood(foodEntry)
            }
        }
    }
}