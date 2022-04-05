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
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val foodItem: Food = NetworkFoodItemFragmentArgs.fromSavedStateHandle(savedStateHandle).foodItem ?: createFood()
    private val position: Int = NetworkFoodItemFragmentArgs.fromSavedStateHandle(savedStateHandle).position

    val ldUser = userRepository.user.asLiveData()
    val uiState = MutableLiveData<NetworkFoodItemUiState>()

    fun loadData() {
        ldUser.value?.let { user ->
            // Get nutrients from food item
            val nutrients = foodItem.foodNutrients
            var protein = 0f
            var fat = 0f
            var carbs = 0f
            var calories = 0f
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

    private fun createFood(): Food {
        return Food(
            fdcId = 454004,
            description = "APPLE",
            lowercaseDescription = "apple",
            dataType = "Branded",
            gtinUpc = "867824000001",
            publishedDate = "2019-04-01",
            brandOwner = "TREECRISP 2 GO",
            ingredients = "CRISP APPLE.",
            marketCountry = "United States",
            foodCategory = "Pre-Packaged Fruit & Vegetables",
            allHighlightFields = "<b>Ingredients</b>: CRISP <em>APPLE</em>.",
            score = 932.4247,
            foodNutrients = listOf(
                FoodNutrient(
                    nutrientId = 1003,
                    nutrientName = "Protein",
                    nutrientNumber = "203",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 0.0
                ),
                FoodNutrient(
                    nutrientId = 1005,
                    nutrientName = "Carbohydrate, by difference",
                    nutrientNumber = "205",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 14.3
                ),
                FoodNutrient(
                    nutrientId = 1008,
                    nutrientName = "Energy",
                    nutrientNumber = "208",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 52.0
                ),
                FoodNutrient(
                    nutrientId = 1004,
                    nutrientName = "Total lipid (fat)",
                    nutrientNumber = "204",
                    unitName = "G",
                    derivationCode = "LCSL",
                    derivationDescription = "Calculated from a less than value per serving size measure",
                    value = 0.65
                ),
            )
        )
    }

}