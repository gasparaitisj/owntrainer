package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    lateinit var currentDay: LocalDate

    val ldMeals: LiveData<List<MealWithFoodEntries>?> = diaryRepository.getAllMealsWithFoodEntries().asLiveData()
    val ldUser: LiveData<User> = switchMap(ldMeals) {
        userRepository.user.asLiveData()
    }

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
    lateinit var allMealsWithFoodEntries: List<MealWithFoodEntries>

    var caloriesConsumed: Double = 0.0
    var proteinConsumed: Double = 0.0
    var fatConsumed: Double = 0.0
    var carbsConsumed: Double = 0.0
    var caloriesPercentage: Double = 0.0
    var proteinPercentage: Double = 0.0
    var fatPercentage: Double = 0.0
    var carbsPercentage: Double = 0.0

    suspend fun calculateData() {
        Timber.d("calculateData() start")
        val meals = mutableListOf<MealWithFoodEntries>()
        caloriesConsumed = 0.0
        proteinConsumed = 0.0
        fatConsumed = 0.0
        carbsConsumed = 0.0
        for (meal in diaryEntryWithMeals.meals) {
            val mealWithFoodEntries = diaryRepository.getMealWithFoodEntriesById(meal.mealId)
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
                Timber.d("Meal calories: " + mealWithFoodEntries.calories)
            }
        }
        Timber.d("Calories: " + caloriesConsumed)
        caloriesPercentage = (caloriesConsumed / user.dailyKcalIntake) * 100
        proteinPercentage = (proteinConsumed / user.dailyProteinIntakeInG) * 100
        fatPercentage = (fatConsumed / user.dailyFatIntakeInG) * 100
        carbsPercentage = (carbsConsumed / user.dailyCarbsIntakeInG) * 100
        mealsWithFoodEntries = meals
        Timber.d("calculateData() end")
    }

    fun createDiaryEntry() {
        // Current day's entry does not exist - insert it to the database
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

    suspend fun addMealToDiary(mealWithFoodEntries: MealWithFoodEntries) {
        diaryRepository.insertDiaryEntryMealCrossRef(
            DiaryEntryMealCrossRef(
                diaryEntryWithMeals.diaryEntry.diaryEntryId,
                mealWithFoodEntries.meal.mealId
            )
        )
    }
}