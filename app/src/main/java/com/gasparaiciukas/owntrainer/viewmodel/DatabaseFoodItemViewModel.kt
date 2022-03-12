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
    val food: FoodEntryParcelable = savedStateHandle["food"]!!

    var carbsPercentage = 0f
    var carbsDailyIntake = 0f
    var calorieDailyIntake = 0f
    var fatPercentage = 0f
    var fatDailyIntake = 0f
    var proteinPercentage = 0f
    var proteinDailyIntake = 0f

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    lateinit var user: User

    fun loadData() {
        val sum = food.carbs + food.fat + food.protein
        carbsPercentage = (food.carbs / sum * 100).toFloat()
        fatPercentage = (food.fat / sum * 100).toFloat()
        proteinPercentage = (food.protein / sum * 100).toFloat()
        calorieDailyIntake = user.dailyKcalIntake.toFloat()
        carbsDailyIntake = user.dailyCarbsIntakeInG.toFloat()
        fatDailyIntake = user.dailyFatIntakeInG.toFloat()
        proteinDailyIntake = user.dailyProteinIntakeInG.toFloat()
    }
}