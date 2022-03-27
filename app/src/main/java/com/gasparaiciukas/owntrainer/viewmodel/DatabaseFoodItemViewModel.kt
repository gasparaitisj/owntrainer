package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DatabaseFoodItemViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val food: FoodEntryParcelable? = savedStateHandle["food"]

    var carbsPercentage = 0f
    var carbsDailyIntake = 0f
    var carbsDailyIntakePercentage = 0f
    var caloriesDailyIntake = 0f
    var caloriesDailyIntakePercentage = 0f
    var fatPercentage = 0f
    var fatDailyIntake = 0f
    var fatDailyIntakePercentage = 0f
    var proteinPercentage = 0f
    var proteinDailyIntake = 0f
    var proteinDailyIntakePercentage = 0f

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    lateinit var user: User

    fun loadData() {
        if (food != null) {
            val sum = food.carbs + food.fat + food.protein
            carbsPercentage = (food.carbs / sum * 100).toFloat()
            fatPercentage = (food.fat / sum * 100).toFloat()
            proteinPercentage = (food.protein / sum * 100).toFloat()

            caloriesDailyIntake = user.dailyKcalIntake.toFloat()
            carbsDailyIntake = user.dailyCarbsIntakeInG.toFloat()
            fatDailyIntake = user.dailyFatIntakeInG.toFloat()
            proteinDailyIntake = user.dailyProteinIntakeInG.toFloat()

            carbsDailyIntakePercentage = (food.carbs / carbsDailyIntake * 100).toFloat()
            caloriesDailyIntakePercentage = (food.calories / caloriesDailyIntake * 100).toFloat()
            fatDailyIntakePercentage = (food.fat / fatDailyIntake * 100).toFloat()
            proteinDailyIntakePercentage = (food.protein / proteinDailyIntake * 100).toFloat()
        }
    }
}