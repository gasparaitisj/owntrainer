
package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.FoodRepository
import com.gasparaiciukas.owntrainer.database.MealRepository
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.network.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddFoodToMealViewModel @Inject internal constructor(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val foodItem: Food? = savedStateHandle["foodItem"]
    private val quantity: Int? = savedStateHandle["quantity"]

    val ldMeals = mealRepository.getMealsWithFoodEntries().asLiveData()
    lateinit var meals: List<MealWithFoodEntries>

    suspend fun addFoodToMeal(mealWithFoodEntries: MealWithFoodEntries) {
        if (foodItem != null && quantity != null) {
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
                mealWithFoodEntries.meal.mealId,
                foodItem.description.toString(),
                calories,
                carbs,
                fat,
                protein,
                quantity.toDouble()
            )
            foodEntry.calories = foodEntry.calculateCalories(foodEntry.caloriesPer100G, foodEntry.quantityInG)
            foodEntry.carbs = foodEntry.calculateCarbs(foodEntry.carbsPer100G, foodEntry.quantityInG)
            foodEntry.fat = foodEntry.calculateFat(foodEntry.fatPer100G, foodEntry.quantityInG)
            foodEntry.protein = foodEntry.calculateProtein(foodEntry.proteinPer100G, foodEntry.quantityInG)
            foodRepository.insertFood(foodEntry)
        }
    }
}