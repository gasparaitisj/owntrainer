package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.fragment.DatabaseFoodItemFragmentArgs
import com.gasparaiciukas.owntrainer.network.Resource
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
@ExperimentalCoroutinesApi
class DatabaseFoodItemViewModel @Inject internal constructor(
    userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var food: FoodEntryParcelable? =
        DatabaseFoodItemFragmentArgs.fromSavedStateHandle(savedStateHandle).food

    val user: Flow<User> = userRepository.user

    val uiState: StateFlow<Resource<DatabaseFoodItemUiState>> = user.mapLatest { u ->
        food?.let { lFood ->
            val sum = lFood.carbs + lFood.fat + lFood.protein
            return@mapLatest Resource.success(
                DatabaseFoodItemUiState(
                    food = lFood,
                    carbsPercentage = (lFood.carbs / sum * 100).toFloat(),
                    carbsDailyIntake = u.dailyCarbsIntakeInG.toFloat(),
                    carbsDailyIntakePercentage = (lFood.carbs / u.dailyCarbsIntakeInG * 100).toFloat(),
                    caloriesDailyIntake = u.dailyKcalIntake.toFloat(),
                    caloriesDailyIntakePercentage = (lFood.calories / u.dailyKcalIntake * 100).toFloat(),
                    fatPercentage = (lFood.fat / sum * 100).toFloat(),
                    fatDailyIntake = u.dailyFatIntakeInG.toFloat(),
                    fatDailyIntakePercentage = (lFood.fat / u.dailyFatIntakeInG * 100).toFloat(),
                    proteinPercentage = (lFood.protein / sum * 100).toFloat(),
                    proteinDailyIntake = u.dailyProteinIntakeInG.toFloat(),
                    proteinDailyIntakePercentage = (lFood.protein / u.dailyProteinIntakeInG * 100).toFloat(),
                )
            )
        }
        Resource.error("Unknown error", null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(null)
    )
}