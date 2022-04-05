package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.fragment.MealItemFragmentArgs
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class MealItemUiState(
    val user: User,
    val mealWithFoodEntries: MealWithFoodEntries,
    val carbsPercentage: Double,
    val fatPercentage: Double,
    val proteinPercentage: Double,
    val carbsDailyIntakePercentage: Double,
    val fatDailyIntakePercentage: Double,
    val proteinDailyIntakePercentage: Double,
    val caloriesDailyIntakePercentage: Double
)

@HiltViewModel
class MealItemViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    val diaryRepository: DiaryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val mealId: Int = MealItemFragmentArgs.fromSavedStateHandle(savedStateHandle).mealId

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    val uiState = MutableLiveData<MealItemUiState>()

    fun loadData(user: User) {
        viewModelScope.launch {
            val mealWithFoodEntries = diaryRepository.getMealWithFoodEntriesById(mealId)
            if (mealWithFoodEntries != null) {
                for (food in mealWithFoodEntries.foodEntries) {
                    Timber.d("Title: ${food.title}, Size: ${mealWithFoodEntries.foodEntries.size}")
                }
                mealWithFoodEntries.meal.calories = mealWithFoodEntries.calories
                mealWithFoodEntries.meal.protein = mealWithFoodEntries.protein
                mealWithFoodEntries.meal.carbs = mealWithFoodEntries.carbs
                mealWithFoodEntries.meal.fat = mealWithFoodEntries.fat

                val sum =
                    mealWithFoodEntries.meal.carbs + mealWithFoodEntries.meal.fat + mealWithFoodEntries.meal.protein
                uiState.postValue(
                    MealItemUiState(
                        user = user,
                        mealWithFoodEntries = mealWithFoodEntries,
                        carbsPercentage = mealWithFoodEntries.meal.carbs / sum * 100,
                        fatPercentage = mealWithFoodEntries.meal.fat / sum * 100,
                        proteinPercentage = mealWithFoodEntries.meal.protein / sum * 100,
                        carbsDailyIntakePercentage = mealWithFoodEntries.meal.carbs / user.dailyCarbsIntakeInG * 100,
                        fatDailyIntakePercentage = mealWithFoodEntries.meal.fat / user.dailyFatIntakeInG * 100,
                        proteinDailyIntakePercentage = mealWithFoodEntries.meal.protein / user.dailyProteinIntakeInG * 100,
                        caloriesDailyIntakePercentage = mealWithFoodEntries.meal.calories / user.dailyKcalIntake * 100
                    )
                )
            }
        }
    }

    suspend fun deleteFoodFromMeal(foodEntryId: Int) {
        diaryRepository.deleteFoodById(foodEntryId)
    }
}