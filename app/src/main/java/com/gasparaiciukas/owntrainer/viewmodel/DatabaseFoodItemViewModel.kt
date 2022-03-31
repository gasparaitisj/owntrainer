package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class DatabaseFoodItemUiState(
    val food: FoodEntryParcelable,
    val carbsPercentage: Float,
    val carbsDailyIntake: Float,
    val carbsDailyIntakePercentage: Float,
    val caloriesDailyIntake: Float,
    val caloriesDailyIntakePercentage: Float,
    val fatPercentage: Float,
    val fatDailyIntake: Float,
    val fatDailyIntakePercentage: Float,
    val proteinPercentage: Float,
    val proteinDailyIntake: Float,
    val proteinDailyIntakePercentage: Float
)

@HiltViewModel
class DatabaseFoodItemViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val food: FoodEntryParcelable? = savedStateHandle["food"]

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    val uiState = MutableLiveData<DatabaseFoodItemUiState>()

    fun loadData() {
        if (food != null) {
            ldUser.value?.let { user ->
                val sum = food.carbs + food.fat + food.protein

                uiState.postValue(
                    DatabaseFoodItemUiState(
                        food = food,
                        carbsPercentage = (food.carbs / sum * 100).toFloat(),
                        carbsDailyIntake = user.dailyCarbsIntakeInG.toFloat(),
                        carbsDailyIntakePercentage = (food.carbs / user.dailyCarbsIntakeInG * 100).toFloat(),
                        caloriesDailyIntake = user.dailyKcalIntake.toFloat(),
                        caloriesDailyIntakePercentage = (food.calories / user.dailyKcalIntake * 100).toFloat(),
                        fatPercentage = (food.fat / sum * 100).toFloat(),
                        fatDailyIntake = user.dailyFatIntakeInG.toFloat(),
                        fatDailyIntakePercentage = (food.fat / user.dailyFatIntakeInG * 100).toFloat(),
                        proteinPercentage = (food.protein / sum * 100).toFloat(),
                        proteinDailyIntake = user.dailyProteinIntakeInG.toFloat(),
                        proteinDailyIntakePercentage = (food.protein / user.dailyProteinIntakeInG * 100).toFloat(),
                    )
                )
            }
        }
    }
}