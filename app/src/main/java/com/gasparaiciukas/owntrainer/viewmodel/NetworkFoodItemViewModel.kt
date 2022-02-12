package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.database.UserRepository
import com.gasparaiciukas.owntrainer.network.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkFoodItemViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val foodItem: Food? = savedStateHandle["foodItem"]
    private val position: Int? = savedStateHandle["position"]

    val ldUser = userRepository.user.asLiveData()
    lateinit var user: User

    var title = ""
    var carbs = 0f
    var carbsPercentage = 0f
    var calories = 0f
    var fat = 0f
    var fatPercentage = 0f
    var protein = 0f
    var proteinPercentage = 0f

    fun loadData() {
        if (foodItem != null) {
            // Get nutrients from food item
            title = foodItem.description.toString()
            val nutrients = foodItem.foodNutrients
            if (nutrients != null) {
                for (nutrient in nutrients) {
                    val nutrientValue = (nutrient.value ?: 0.0).toFloat()
                    if (nutrient.nutrientId == 1003) protein = nutrientValue
                    if (nutrient.nutrientId == 1004) fat = nutrientValue
                    if (nutrient.nutrientId == 1005) carbs = nutrientValue
                    if (nutrient.nutrientId == 1008) calories = nutrientValue
                }
            }

            // Calculate percentage of each item
            val sum = carbs + fat + protein
            carbsPercentage = carbs / sum * 100
            fatPercentage = fat / sum * 100
            proteinPercentage = protein / sum * 100
        }
    }
}