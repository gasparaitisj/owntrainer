package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.DiaryEntryWithMeals
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    lateinit var currentDay: LocalDate

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    val ldDiaryEntryWithMeals: LiveData<DiaryEntryWithMeals> = switchMap(ldUser) {
        if (::flUser.isInitialized) {
            flUser.flatMapLatest {
                diaryRepository.getDiaryEntryWithMeals(it.year, it.dayOfYear)
            }.asLiveData()
        } else {
            MutableLiveData()
        }
    }

    lateinit var flUser: MutableStateFlow<User>
    lateinit var user: User
    lateinit var diaryEntryWithMeals: DiaryEntryWithMeals
    lateinit var mealsWithFoodEntries: List<MealWithFoodEntries>

    var caloriesConsumed: Double = 0.0
    var proteinConsumed: Double = 0.0
    var fatConsumed: Double = 0.0
    var carbsConsumed: Double = 0.0
    var caloriesPercentage: Double = 0.0
    var proteinPercentage: Double = 0.0
    var fatPercentage: Double = 0.0
    var carbsPercentage: Double = 0.0

    suspend fun calculateData() {
        val meals = mutableListOf<MealWithFoodEntries>()
        caloriesConsumed = 0.0
        proteinConsumed = 0.0
        fatConsumed = 0.0
        carbsConsumed = 0.0
        for (meal in diaryEntryWithMeals.meals) {
            val mealWithFoodEntries = diaryRepository.getMealWithFoodEntriesById(meal.mealId)
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
        caloriesPercentage = (caloriesConsumed / user.dailyKcalIntake) * 100
        proteinPercentage = (proteinConsumed / user.dailyProteinIntakeInG) * 100
        fatPercentage = (fatConsumed / user.dailyFatIntakeInG) * 100
        carbsPercentage = (carbsConsumed / user.dailyCarbsIntakeInG) * 100
        mealsWithFoodEntries = meals
    }

    fun createDiaryEntry() {
        // Current day's entry does not exist - insert it to the database
        viewModelScope.launch {
            val diaryEntry = DiaryEntry(
                currentDay.year,
                currentDay.dayOfYear,
                currentDay.dayOfWeek.value,
                currentDay.monthValue,
                currentDay.dayOfMonth
            )
            diaryRepository.insertDiaryEntry(diaryEntry)
        }
    }

    suspend fun updateUserToPreviousDay() =
        updateUserDate(currentDay.minusDays(1))

    suspend fun updateUserToCurrentDay() =
        updateUserDate(LocalDate.now())

    suspend fun updateUserToNextDay() =
        updateUserDate(currentDay.plusDays(1))

    private suspend fun updateUserDate(currentDay: LocalDate) {
        user.year = currentDay.year
        user.month = currentDay.monthValue
        user.dayOfYear = currentDay.dayOfYear
        user.dayOfMonth = currentDay.dayOfMonth
        user.dayOfWeek = currentDay.dayOfWeek.value
        userRepository.updateUser(user)
    }

    suspend fun deleteMealFromDiary(diaryEntryId: Int, mealId: Int) {
        diaryRepository.deleteDiaryEntryMealCrossRef(diaryEntryId, mealId)
    }
}