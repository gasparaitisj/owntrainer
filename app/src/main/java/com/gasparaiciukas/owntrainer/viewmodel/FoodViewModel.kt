package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.utils.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val proteinPercentage: Float,
)

@HiltViewModel
class FoodViewModel @Inject internal constructor(
    val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var foodItem: Food? = null
    var quantity = 100

    val ldMeals = diaryRepository.getAllMealsWithFoodEntries().asLiveData()
    val ldUser = userRepository.user.asLiveData()
    val networkFoodItemUiState = MutableLiveData<NetworkFoodItemUiState>()


    val ldResponse: LiveData<Resource<GetResponse>> get() = _ldResponse
    private val _ldResponse: MutableLiveData<Resource<GetResponse>> = MutableLiveData()

    var pageNumber = 1
    var foodsResponse: Resource<GetResponse>? = null

    fun getFoods(query: String) {
        viewModelScope.launch {
            _ldResponse.postValue(Resource.loading(null))
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
            _ldResponse.postValue(foodsResponse ?: response)
        }
    }

    fun clearFoods() {
        _ldResponse.value = Resource.success(null)
    }

    fun onQueryChanged() {
        pageNumber = 1
        foodsResponse = null
    }

    fun loadNetworkFoodItemUiState() {
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
            networkFoodItemUiState.postValue(
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