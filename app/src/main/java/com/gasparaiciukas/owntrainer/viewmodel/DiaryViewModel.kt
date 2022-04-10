package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DiaryUiState(
    val meals: List<MealWithFoodEntries>,
    val user: User,
    val diaryEntryWithMeals: DiaryEntryWithMeals,
    val proteinConsumed: Double,
    val fatConsumed: Double,
    val carbsConsumed: Double,
    val caloriesConsumed: Double,
    val caloriesPercentage: Double,
    val proteinPercentage: Double,
    val fatPercentage: Double,
    val carbsPercentage: Double
)

@ExperimentalCoroutinesApi
@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    val ldAllMeals = diaryRepository.getAllMealsWithFoodEntries().asLiveData()
    val ldUser: LiveData<User> = switchMap(ldAllMeals) {
        userRepository.user.asLiveData()
    }

    val ldDiaryEntryWithMeals: LiveData<DiaryEntryWithMeals> = switchMap(ldUser) {
        diaryRepository.getDiaryEntryWithMeals(it.year, it.dayOfYear).asLiveData()
    }

    val uiState = MutableLiveData<DiaryUiState>()

    fun calculateData() {
        viewModelScope.launch {
            val meals = mutableListOf<MealWithFoodEntries>()
            var caloriesConsumed = 0.0
            var proteinConsumed = 0.0
            var fatConsumed = 0.0
            var carbsConsumed = 0.0
            safeLet(ldDiaryEntryWithMeals.value, ldUser.value) { diaryEntryWithMeals, user ->
                for (meal in diaryEntryWithMeals.meals) {
                    val mealWithFoodEntries =
                        diaryRepository.getMealWithFoodEntriesById(meal.mealId)
                    if (mealWithFoodEntries != null) {
                        mealWithFoodEntries.meal.calories = mealWithFoodEntries.calories
                        caloriesConsumed += mealWithFoodEntries.calories
                        mealWithFoodEntries.meal.protein = mealWithFoodEntries.protein
                        proteinConsumed += mealWithFoodEntries.protein
                        mealWithFoodEntries.meal.carbs = mealWithFoodEntries.carbs
                        carbsConsumed += mealWithFoodEntries.carbs
                        mealWithFoodEntries.meal.fat = mealWithFoodEntries.fat
                        fatConsumed += mealWithFoodEntries.fat
                        meals.add(mealWithFoodEntries)
                    }
                }
                uiState.postValue(
                    DiaryUiState(
                        meals = meals,
                        user = user,
                        diaryEntryWithMeals = diaryEntryWithMeals,
                        proteinConsumed = proteinConsumed,
                        fatConsumed = fatConsumed,
                        carbsConsumed = carbsConsumed,
                        caloriesConsumed = caloriesConsumed,
                        caloriesPercentage = (caloriesConsumed / user.dailyKcalIntake) * 100,
                        proteinPercentage = (proteinConsumed / user.dailyProteinIntakeInG) * 100,
                        fatPercentage = (fatConsumed / user.dailyFatIntakeInG) * 100,
                        carbsPercentage = (carbsConsumed / user.dailyCarbsIntakeInG) * 100
                    )
                )
            }
        }
    }

    fun createDiaryEntry() {
        // Current day's entry does not exist - insert it to the database
        ldUser.value?.let {
            val currentDay = LocalDate.ofYearDay(it.year, it.dayOfYear)
            viewModelScope.launch {
                val diaryEntry = DiaryEntry(
                    year = currentDay.year,
                    dayOfYear = currentDay.dayOfYear,
                    dayOfWeek = currentDay.dayOfWeek.value,
                    monthOfYear = currentDay.monthValue,
                    dayOfMonth = currentDay.dayOfMonth
                )
                diaryRepository.insertDiaryEntry(diaryEntry)
            }
        }
    }

    fun updateUserToPreviousDay() {
        ldUser.value?.let {
            val currentDay = LocalDate.ofYearDay(it.year, it.dayOfYear)
            updateUserDate(currentDay.minusDays(1))
        }
    }

    fun updateUserToCurrentDay() {
        updateUserDate(LocalDate.now())
    }

    fun updateUserToNextDay() {
        ldUser.value?.let {
            val currentDay = LocalDate.ofYearDay(it.year, it.dayOfYear)
            updateUserDate(currentDay.plusDays(1))
        }
    }

    private fun updateUserDate(currentDay: LocalDate) {
        viewModelScope.launch {
            val user = ldUser.value
            if (user != null) {
                user.year = currentDay.year
                user.month = currentDay.monthValue
                user.dayOfYear = currentDay.dayOfYear
                user.dayOfMonth = currentDay.dayOfMonth
                user.dayOfWeek = currentDay.dayOfWeek.value
                userRepository.updateUser(user)
            }
        }
    }

    fun deleteMealFromDiary(diaryEntryId: Int, mealId: Int) {
        viewModelScope.launch {
            diaryRepository.deleteDiaryEntryMealCrossRef(diaryEntryId, mealId)
        }
    }

    fun addMealToDiary(mealWithFoodEntries: MealWithFoodEntries) {
        viewModelScope.launch {
            ldDiaryEntryWithMeals.value?.let { diaryEntryWithMeals ->
                diaryRepository.insertDiaryEntryMealCrossRef(
                    DiaryEntryMealCrossRef(
                        diaryEntryWithMeals.diaryEntry.diaryEntryId,
                        mealWithFoodEntries.meal.mealId
                    )
                )
            }
        }
    }
}