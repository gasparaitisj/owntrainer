
package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealRepository
import com.gasparaiciukas.owntrainer.database.UserRepository
import com.gasparaiciukas.owntrainer.network.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectMealItemViewModel @Inject internal constructor(
    private val mealRepository: MealRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val foodItem: Food? = savedStateHandle["foodItem"]
    private val quantity: Int? = savedStateHandle["quantity"]

//    val meals: List<Meal> = realm.where(Meal::class.java).findAll()
    val meals: List<Meal> = listOf()

    fun addFoodToMeal(meal: Meal) {
        viewModelScope.launch {

        }
//        val foodList = meal.foodList
//        val food = FoodEntry()
//        var protein = 0.0
//        var fat = 0.0
//        var carbs = 0.0
//        var calories = 0.0
//
//        val nutrients = foodItem.foodNutrients
//        if (nutrients != null) {
//            for (nutrient in nutrients) {
//                if (nutrient.nutrientId == 1003) protein = (nutrient.value ?: 0.0)
//                if (nutrient.nutrientId == 1004) fat = (nutrient.value ?: 0.0)
//                if (nutrient.nutrientId == 1005) carbs = (nutrient.value ?: 0.0)
//                if (nutrient.nutrientId == 1008) calories = (nutrient.value ?: 0.0)
//            }
//        }
//        food.title = foodItem.description.toString()
//        food.caloriesPer100G = calories
//        food.carbsPer100G = carbs
//        food.fatPer100G = fat
//        food.proteinPer100G = protein
//        food.quantityInG = quantity
//        food.calories = food.calculateCalories(food.caloriesPer100G, food.quantityInG)
//        food.carbs = food.calculateCarbs(food.carbsPer100G, food.quantityInG)
//        food.fat = food.calculateFat(food.fatPer100G, food.quantityInG)
//        food.protein = food.calculateProtein(food.proteinPer100G, food.quantityInG)
//
//        // Write to database
//        realm.executeTransaction {
//            foodList.add(food)
//            meal.foodList = foodList
//        }
    }
}