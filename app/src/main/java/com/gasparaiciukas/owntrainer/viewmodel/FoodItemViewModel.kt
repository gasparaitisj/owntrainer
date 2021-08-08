package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.network.FoodApi
import io.realm.Realm

class FoodItemViewModel constructor(private val bundle: Bundle) : ViewModel() {
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

    private val foodItem: FoodApi = bundle.getParcelable("foodItem")!!
    private val position: Int = bundle.getInt("position")

    init {
        fetchData()
    }

    private fun fetchData() {
        // Get nutrients from food item
        carbs = foodItem.nutrients.carbs.toFloat()
        calories = foodItem.nutrients.calories.toFloat()
        fat = foodItem.nutrients.fat.toFloat()
        protein = foodItem.nutrients.protein.toFloat()

        // Calculate percentage of each item
        val sum = carbs + fat + protein
        carbsPercentage = carbs / sum * 100
        fatPercentage = fat / sum * 100
        proteinPercentage = protein / sum * 100

        // Get daily intake percentages
        val realm = Realm.getDefaultInstance()
        val user = realm.where(User::class.java)
            .equalTo("userId", "user")
            .findFirst()
        if (user != null) {
            calorieDailyIntake = user.dailyKcalIntake.toFloat()
            carbsDailyIntake = user.dailyCarbsIntakeInG.toFloat()
            fatDailyIntake = user.dailyFatIntakeInG.toFloat()
            proteinDailyIntake = user.dailyProteinIntakeInG.toFloat()
        }
        realm.close()
    }
}