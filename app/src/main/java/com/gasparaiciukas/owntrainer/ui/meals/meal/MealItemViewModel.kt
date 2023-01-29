package com.gasparaiciukas.owntrainer.ui.meals.meal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.database.User
import com.gasparaiciukas.owntrainer.utils.network.Resource
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.utils.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    val caloriesDailyIntakePercentage: Float
)

@HiltViewModel
@ExperimentalCoroutinesApi
class MealItemViewModel @Inject internal constructor(
    userRepository: UserRepository,
    val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val mealId: Int = MealItemFragmentArgs.fromSavedStateHandle(savedStateHandle).mealId

    val user = userRepository.user

    val mealWithFoodEntries = diaryRepository.getAllMealsWithFoodEntries().map {
        it?.firstOrNull { m -> m.meal.mealId == mealId }
    }

    val uiState = combine(user, mealWithFoodEntries) { lUser, lMealWithFoodEntries ->
        lMealWithFoodEntries?.let { it ->
            it.meal.calories = it.calories
            it.meal.protein = it.protein
            it.meal.carbs = it.carbs
            it.meal.fat = it.fat

            val sum =
                it.meal.carbs + it.meal.fat + it.meal.protein
            return@combine Resource.success(
                MealItemUiState(
                    user = lUser,
                    mealWithFoodEntries = it,
                    carbsPercentage = (it.meal.carbs / sum * 100).toFloat(),
                    fatPercentage = (it.meal.fat / sum * 100).toFloat(),
                    proteinPercentage = (it.meal.protein / sum * 100).toFloat(),
                    carbsDailyIntakePercentage = (it.meal.carbs / lUser.dailyCarbsIntakeInG * 100).toFloat(),
                    fatDailyIntakePercentage = (it.meal.fat / lUser.dailyFatIntakeInG * 100).toFloat(),
                    proteinDailyIntakePercentage = (it.meal.protein / lUser.dailyProteinIntakeInG * 100).toFloat(),
                    caloriesDailyIntakePercentage = (it.meal.calories / lUser.dailyKcalIntake * 100).toFloat()
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(null)
    )

    fun deleteFoodFromMeal(foodEntryId: Int) {
        viewModelScope.launch {
            diaryRepository.deleteFoodById(foodEntryId)
        }
    }
}
