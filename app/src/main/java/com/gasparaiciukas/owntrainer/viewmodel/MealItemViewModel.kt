package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.repository.DefaultDiaryRepository
import com.gasparaiciukas.owntrainer.repository.DefaultFoodRepository
import com.gasparaiciukas.owntrainer.repository.DefaultUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MealItemViewModel @Inject internal constructor(
    private val userRepository: DefaultUserRepository,
    private val foodRepository: DefaultFoodRepository,
    private val diaryRepository: DefaultDiaryRepository,
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
            mealWithFoodEntries = diaryRepository.getMealWithFoodEntriesById(mealId)
            for (food in mealWithFoodEntries.foodEntries) {
                Timber.d("Title: ${food.title}, Size: ${mealWithFoodEntries.foodEntries.size}")
            }
            mealWithFoodEntries.meal.calories = mealWithFoodEntries.calories
            mealWithFoodEntries.meal.protein = mealWithFoodEntries.protein
            mealWithFoodEntries.meal.carbs = mealWithFoodEntries.carbs
            mealWithFoodEntries.meal.fat = mealWithFoodEntries.fat

            val sum =
                mealWithFoodEntries.meal.carbs + mealWithFoodEntries.meal.fat + mealWithFoodEntries.meal.protein
            carbsPercentage = (mealWithFoodEntries.meal.carbs / sum * 100).toFloat()
            fatPercentage = (mealWithFoodEntries.meal.fat / sum * 100).toFloat()
            proteinPercentage = (mealWithFoodEntries.meal.protein / sum * 100).toFloat()
        }
    }

    suspend fun deleteFoodFromMeal(foodEntryId: Int) {
        foodRepository.deleteFoodById(foodEntryId)
    }
}