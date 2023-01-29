package com.gasparaiciukas.owntrainer.utils.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.utils.database.FoodEntry
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.database.User
import com.gasparaiciukas.owntrainer.utils.network.Food
import com.gasparaiciukas.owntrainer.utils.network.GetResponse
import com.gasparaiciukas.owntrainer.utils.network.Resource
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.utils.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    val proteinPercentage: Float
)

@HiltViewModel
class FoodViewModel @Inject internal constructor(
    val diaryRepository: DiaryRepository,
    userRepository: UserRepository
) : ViewModel() {

    var foodItem: Food? = null
    var quantity = 100

    val meals = diaryRepository.getAllMealsWithFoodEntries()
    val user = userRepository.user

    val networkFoodItemUiState = combine(meals, user) { lMeals, lUser ->
        safeLet(lMeals, foodItem) { _, foodItem ->
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
            return@combine Resource.success(
                NetworkFoodItemUiState(
                    user = lUser,
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(null)
    )

    private val _response: MutableStateFlow<Resource<GetResponse>> =
        MutableStateFlow(Resource.success())
    val response: StateFlow<Resource<GetResponse>> = _response.asStateFlow()

    var pageNumber = 1
    var foodsResponse: Resource<GetResponse>? = null

    fun getFoods(query: String) {
        viewModelScope.launch {
            _response.value = Resource.loading(null)
            val response = diaryRepository.getFoods(
                query = query,
                dataType = Constants.Api.DataType.DATATYPE_BRANDED + "," +
                    Constants.Api.DataType.DATATYPE_SR_LEGACY,
                numberOfResultsPerPage = Constants.Api.QUERY_PAGE_SIZE,
                pageSize = Constants.Api.QUERY_PAGE_SIZE,
                pageNumber = pageNumber,
                requireAllWords = true
            )
            pageNumber++
            if (foodsResponse == null) {
                foodsResponse = response
            } else {
                val oldFoods = foodsResponse?.data?.foods
                val newFoods = response.data?.foods
                if (newFoods != null) {
                    oldFoods?.addAll(newFoods)
                }
            }
            _response.value = foodsResponse ?: response
        }
    }

    fun clearFoods() {
        _response.value = Resource.success(null)
    }

    fun onQueryChanged() {
        pageNumber = 1
        foodsResponse = null
    }

    fun addFoodToMeal(mealWithFoodEntries: MealWithFoodEntries) {
        viewModelScope.launch {
            foodItem?.let { foodItem ->
                var protein = 0.0
                var fat = 0.0
                var carbs = 0.0
                var calories = 0.0

                val nutrients = foodItem.foodNutrients
                if (nutrients != null) {
                    for (nutrient in nutrients) {
                        if (nutrient.nutrientId == 1003) protein = (nutrient.value ?: 0.0)
                        if (nutrient.nutrientId == 1004) fat = (nutrient.value ?: 0.0)
                        if (nutrient.nutrientId == 1005) carbs = (nutrient.value ?: 0.0)
                        if (nutrient.nutrientId == 1008) calories = (nutrient.value ?: 0.0)
                    }
                }
                val foodEntry = FoodEntry(
                    mealId = mealWithFoodEntries.meal.mealId,
                    title = foodItem.description.toString(),
                    caloriesPer100G = calories,
                    carbsPer100G = carbs,
                    fatPer100G = fat,
                    proteinPer100G = protein,
                    quantityInG = quantity.toDouble()
                )
                diaryRepository.insertFood(foodEntry)
            }
        }
    }
}
