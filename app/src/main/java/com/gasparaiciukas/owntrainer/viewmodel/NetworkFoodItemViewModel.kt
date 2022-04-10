package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.fragment.NetworkFoodItemFragmentArgs
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class NetworkFoodItemUiState(
    val user: User,
    val foodItem: Food,
    val title: String,
    val carbs: Float,
    val carbsPercentage: Float,
    val calories: Float,
    val fat: Float,
    val fatPercentage: Float,
    val protein: Float,
    val proteinPercentage: Float,
)

@HiltViewModel
class NetworkFoodItemViewModel @Inject internal constructor(
    userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val foodItem: Food? = NetworkFoodItemFragmentArgs.fromSavedStateHandle(savedStateHandle).foodItem
    private val position: Int = NetworkFoodItemFragmentArgs.fromSavedStateHandle(savedStateHandle).position

    val ldUser = userRepository.user.asLiveData()
    val uiState = MutableLiveData<NetworkFoodItemUiState>()

    fun loadData() {
        safeLet(ldUser.value, foodItem) { user, foodItem ->
            // Get nutrients from food item
            val nutrients = foodItem.foodNutrients
            var protein = 0f
            var fat = 0f
            var carbs = 0f
            var calories = 0f
            nutrients?.forEach { nutrient ->
                val nutrientValue = (nutrient.value ?: 0.0).toFloat()
                if (nutrient.nutrientId == 1003) protein = nutrientValue
                if (nutrient.nutrientId == 1004) fat = nutrientValue
                if (nutrient.nutrientId == 1005) carbs = nutrientValue
                if (nutrient.nutrientId == 1008) calories = nutrientValue
            }
            // Calculate percentage of each item
            val sum = carbs + fat + protein
            uiState.postValue(
                NetworkFoodItemUiState(
                    user = user,
                    foodItem = foodItem,
                    title = foodItem.description.toString(),
                    carbs = carbs,
                    carbsPercentage = carbs / sum * 100,
                    calories = calories,
                    fat = fat,
                    fatPercentage = fat / sum * 100,
                    protein = protein,
                    proteinPercentage = protein / sum * 100
                )
            )
        }
    }
}