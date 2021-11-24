package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.network.Food
import io.realm.Realm

class NetworkFoodItemViewModel constructor(private val bundle: Bundle) : ViewModel() {
    var title = ""
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

    private val foodItem: Food = bundle.getParcelable("foodItem")!!
    private val position: Int = bundle.getInt("position")

    init {
        fetchData()
    }

    private fun fetchData() {
        // Get nutrients from food item
        title = foodItem.description.toString()
        calories = 0.0f
        protein = 0.0f
        fat = 0.0f
        carbs = 0.0f

        val nutrients = foodItem.foodNutrients
        if (nutrients != null) {
            for (nutrient in nutrients) {
                if (nutrient.nutrientId == 1003) protein = (nutrient.value ?: 0.0).toFloat()
                if (nutrient.nutrientId == 1004) fat = (nutrient.value ?: 0.0).toFloat()
                if (nutrient.nutrientId == 1005) carbs = (nutrient.value ?: 0.0).toFloat()
                if (nutrient.nutrientId == 1008) calories = (nutrient.value ?: 0.0).toFloat()
            }
        }

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