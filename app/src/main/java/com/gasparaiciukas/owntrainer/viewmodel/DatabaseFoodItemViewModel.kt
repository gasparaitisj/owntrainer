package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import io.realm.Realm

class DatabaseFoodItemViewModel constructor(private val bundle: Bundle) : ViewModel() {

    var carbsPercentage = 0f
    var carbsDailyIntake = 0f
    var calorieDailyIntake = 0f
    var fatPercentage = 0f
    var fatDailyIntake = 0f
    var proteinPercentage = 0f
    var proteinDailyIntake = 0f
    val food: FoodEntryParcelable = bundle.getParcelable("food")!!

    init {
        fetchData()
    }

    private fun fetchData() {
        // Calculate percentage of each item
        val sum = food.carbs + food.fat + food.protein
        carbsPercentage = (food.carbs / sum * 100).toFloat()
        fatPercentage = (food.fat / sum * 100).toFloat()
        proteinPercentage = (food.protein / sum * 100).toFloat()

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