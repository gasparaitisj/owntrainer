package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.Food
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.network.FoodApi
import io.realm.Realm

class SelectMealItemViewModel constructor(private val bundle: Bundle) : ViewModel() {
    private val foodItem: FoodApi = bundle.getParcelable("foodItem")!!
    private val quantity = bundle.getInt("quantity").toDouble()

    private val realm = Realm.getDefaultInstance()
    val meals: List<Meal> = realm.where(Meal::class.java).findAll()

    fun addFoodToMeal(meal: Meal) {
        val foodList = meal.foodList
        val food = Food()
        food.title = foodItem.label
        food.caloriesPer100G = foodItem.nutrients.calories
        food.carbsPer100G = foodItem.nutrients.carbs
        food.fatPer100G = foodItem.nutrients.fat
        food.proteinPer100G = foodItem.nutrients.protein
        food.quantityInG = quantity
        food.calories = food.calculateCalories(food.caloriesPer100G, food.quantityInG)
        food.carbs = food.calculateCarbs(food.carbsPer100G, food.quantityInG)
        food.fat = food.calculateFat(food.fatPer100G, food.quantityInG)
        food.protein = food.calculateProtein(food.proteinPer100G, food.quantityInG)

        // Write to database
        realm.executeTransaction {
            foodList.add(food)
            meal.foodList = foodList
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}