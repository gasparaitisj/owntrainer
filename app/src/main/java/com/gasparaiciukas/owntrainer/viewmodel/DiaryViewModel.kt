package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val diaryEntryRepository: DiaryEntryRepository,
    private val mealRepository: MealRepository
) : ViewModel() {

    lateinit var currentDay: LocalDate

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    lateinit var flUser: MutableStateFlow<User>
    lateinit var user: User

    lateinit var diaryEntryWithMeals: DiaryEntryWithMeals
    lateinit var flDiaryEntryWithMeals: Flow<DiaryEntryWithMeals>
    lateinit var ldDiaryEntryWithMeals: LiveData<DiaryEntryWithMeals>

    lateinit var ldMealsWithFoodEntries: MutableLiveData<List<MealWithFoodEntries>>
    lateinit var mealsWithFoodEntries: List<MealWithFoodEntries>

    var caloriesConsumed: Double = 0.0
    var proteinConsumed: Double = 0.0
    var fatConsumed: Double = 0.0
    var carbsConsumed: Double = 0.0
    var caloriesPercentage: Double = 0.0
    var proteinPercentage: Double = 0.0
    var fatPercentage: Double = 0.0
    var carbsPercentage: Double = 0.0


    fun loadData() {
        flDiaryEntryWithMeals = flUser.flatMapLatest {
            diaryEntryRepository.getDiaryEntryWithMeals(it.currentYear, it.currentDayOfYear)
        }
        ldDiaryEntryWithMeals = flDiaryEntryWithMeals.asLiveData()
    }

    suspend fun calculateData() {
        val meals = mutableListOf<MealWithFoodEntries>()
        caloriesConsumed = 0.0
        proteinConsumed = 0.0
        fatConsumed = 0.0
        carbsConsumed = 0.0
        for (meal in diaryEntryWithMeals.meals) {
            val mealWithFoodEntries = mealRepository.getMealWithFoodEntriesById(meal.mealId)
            mealWithFoodEntries.meal.calories = mealWithFoodEntries.calculateCalories()
            caloriesConsumed += mealWithFoodEntries.calculateCalories()
            mealWithFoodEntries.meal.protein = mealWithFoodEntries.calculateProtein()
            proteinConsumed += mealWithFoodEntries.calculateProtein()
            mealWithFoodEntries.meal.carbs = mealWithFoodEntries.calculateCarbs()
            carbsConsumed += mealWithFoodEntries.calculateCarbs()
            mealWithFoodEntries.meal.fat = mealWithFoodEntries.calculateFat()
            fatConsumed += mealWithFoodEntries.calculateFat()

            meals.add(mealWithFoodEntries)
            Timber.d("Calories: ${mealWithFoodEntries.meal.calories}")
        }
        caloriesPercentage = (caloriesConsumed / user.dailyKcalIntake) * 100
        proteinPercentage = (proteinConsumed / user.dailyProteinIntakeInG) * 100
        fatPercentage = (fatConsumed / user.dailyFatIntakeInG) * 100
        carbsPercentage = (carbsConsumed / user.dailyCarbsIntakeInG) * 100

        ldMealsWithFoodEntries = MutableLiveData(meals)
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
            diaryEntryRepository.insertDiaryEntry(diaryEntry)
        }
    }

    suspend fun updateUserToPreviousDay() {
        currentDay = currentDay.minusDays(1) // subtract 1 day from current day
        val user = ldUser.value
        if (user != null) {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDayOfYear = currentDay.dayOfYear
            user.currentDayOfMonth = currentDay.dayOfMonth
            user.currentDayOfWeek = currentDay.dayOfWeek.value
            userRepository.updateUser(user)
        }
    }

    suspend fun updateUserToCurrentDay() {
        currentDay = LocalDate.now()
        val user = ldUser.value
        if (user != null) {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDayOfYear = currentDay.dayOfYear
            user.currentDayOfMonth = currentDay.dayOfMonth
            user.currentDayOfWeek = currentDay.dayOfWeek.value
            userRepository.updateUser(user)
        }
    }

    suspend fun updateUserToNextDay() {
        currentDay = currentDay.plusDays(1)
        val user = ldUser.value
        if (user != null) {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDayOfYear = currentDay.dayOfYear
            user.currentDayOfMonth = currentDay.dayOfMonth
            user.currentDayOfWeek = currentDay.dayOfWeek.value
            userRepository.updateUser(user)
        }
    }

    suspend fun deleteMealFromDiary(diaryEntryId: Int, mealId: Int) {
        diaryEntryRepository.deleteDiaryEntryMealCrossRef(diaryEntryId, mealId)
    }
}