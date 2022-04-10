package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.fragment.MealItemFragmentArgs
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealItemUiState(
    val user: User,
    val mealWithFoodEntries: MealWithFoodEntries,
    val carbsPercentage: Float,
    val fatPercentage: Float,
    val proteinPercentage: Float,
    val carbsDailyIntakePercentage: Float,
    val fatDailyIntakePercentage: Float,
    val proteinDailyIntakePercentage: Float,
    val caloriesDailyIntakePercentage: Float,
)

@HiltViewModel
class MealItemViewModel @Inject internal constructor(
    userRepository: UserRepository,
    val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val mealId: Int = MealItemFragmentArgs.fromSavedStateHandle(savedStateHandle).mealId

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    val uiState = MutableLiveData<MealItemUiState>()

    fun loadData(user: User) {
        viewModelScope.launch {
            val mealWithFoodEntries = diaryRepository.getMealWithFoodEntriesById(mealId)
            if (mealWithFoodEntries != null) {
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
                        carbsPercentage = (mealWithFoodEntries.meal.carbs / sum * 100).toFloat(),
                        fatPercentage = (mealWithFoodEntries.meal.fat / sum * 100).toFloat(),
                        proteinPercentage = (mealWithFoodEntries.meal.protein / sum * 100).toFloat(),
                        carbsDailyIntakePercentage = (mealWithFoodEntries.meal.carbs / user.dailyCarbsIntakeInG * 100).toFloat(),
                        fatDailyIntakePercentage = (mealWithFoodEntries.meal.fat / user.dailyFatIntakeInG * 100).toFloat(),
                        proteinDailyIntakePercentage = (mealWithFoodEntries.meal.protein / user.dailyProteinIntakeInG * 100).toFloat(),
                        caloriesDailyIntakePercentage = (mealWithFoodEntries.meal.calories / user.dailyKcalIntake * 100).toFloat()
                    )
                )
            }
        }
    }

    fun deleteFoodFromMeal(foodEntryId: Int) {
        viewModelScope.launch {
            diaryRepository.deleteFoodById(foodEntryId)
        }
    }
}