package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.User
import io.realm.Realm

class MealItemViewModel constructor(private val bundle: Bundle) : ViewModel() {
    var carbs = 0f
    var carbsPercentage = 0f
    var carbsDailyIntake = 0f
    var calories = 0f
    var calorieDailyIntake = 0f
    var fat = 0f
    var fatPercentage = 0f
    var fatDailyIntake = 0f
    var protein = 0f
    var proteinPercentage = 0f
    var proteinDailyIntake = 0f
    val quantity = 0
    lateinit var meals: List<Meal>
    lateinit var foodList: List<FoodEntry>
    var position: Int = bundle.getInt("position")

    init {
        fetchData()
    }

    private fun fetchData() {
        val realm = Realm.getDefaultInstance()
        meals = realm.where(Meal::class.java).findAll()
        foodList = meals[position].foodList

        // Get nutrients from food item
        carbs = meals[position].calculateCarbs().toFloat()
        calories = meals[position].calculateCalories().toFloat()
        fat = meals[position].calculateFat().toFloat()
        protein = meals[position].calculateProtein().toFloat()

        // Calculate percentage of each item
        val sum = carbs + fat + protein
        carbsPercentage = carbs / sum * 100
        fatPercentage = fat / sum * 100
        proteinPercentage = protein / sum * 100

        // Get daily intake percentages
        val u = realm.where(User::class.java)
            .equalTo("userId", "user")
            .findFirst()

        if (u != null) {
            calorieDailyIntake = u.dailyKcalIntake.toFloat()
            carbsDailyIntake = u.dailyCarbsIntakeInG.toFloat()
            fatDailyIntake = u.dailyFatIntakeInG.toFloat()
            proteinDailyIntake = u.dailyProteinIntakeInG.toFloat()
        }
        realm.close()
    }
}