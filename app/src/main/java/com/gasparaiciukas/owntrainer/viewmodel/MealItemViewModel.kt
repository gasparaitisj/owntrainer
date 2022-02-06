package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MealItemViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val mealId: Int? = savedStateHandle["mealId"]

    var carbsPercentage = 0f
    var fatPercentage = 0f
    var proteinPercentage = 0f
    val quantity = 0

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    lateinit var user: User

    lateinit var mealWithFoodEntries: MealWithFoodEntries

    suspend fun loadData() {
        if (mealId != null) {
            mealWithFoodEntries = mealRepository.getMealWithFoodEntriesById(mealId)
            for (food in mealWithFoodEntries.foodEntries) {
                Timber.d("Title: ${food.title}, Size: ${mealWithFoodEntries.foodEntries.size}")
            }
            mealWithFoodEntries.meal.calories = mealWithFoodEntries.calculateCalories()
            mealWithFoodEntries.meal.protein = mealWithFoodEntries.calculateProtein()
            mealWithFoodEntries.meal.carbs = mealWithFoodEntries.calculateCarbs()
            mealWithFoodEntries.meal.fat = mealWithFoodEntries.calculateFat()

            val sum = mealWithFoodEntries.meal.carbs + mealWithFoodEntries.meal.fat + mealWithFoodEntries.meal.protein
            carbsPercentage = (mealWithFoodEntries.meal.carbs / sum * 100).toFloat()
            fatPercentage = (mealWithFoodEntries.meal.fat / sum * 100).toFloat()
            proteinPercentage = (mealWithFoodEntries.meal.protein / sum * 100).toFloat()
        }
    }

    suspend fun deleteFoodFromMeal(foodEntryId: Int) {
        foodRepository.deleteFoodById(foodEntryId)
    }
}