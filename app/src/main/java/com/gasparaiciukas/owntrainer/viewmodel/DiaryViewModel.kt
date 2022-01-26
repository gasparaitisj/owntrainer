package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val diaryEntryRepository: DiaryEntryRepository
) : ViewModel() {

    lateinit var currentDay: LocalDate

    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    lateinit var flUser: MutableStateFlow<User>
    lateinit var user: User

    lateinit var diaryEntryWithMeals: DiaryEntryWithMeals
    lateinit var flDiaryEntryWithMeals: Flow<DiaryEntryWithMeals>
    lateinit var ldDiaryEntryWithMeals: LiveData<DiaryEntryWithMeals>

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

    fun calculateData() {
        caloriesConsumed = diaryEntryWithMeals.calculateTotalCalories(diaryEntryWithMeals.meals)
        proteinConsumed = diaryEntryWithMeals.calculateTotalProtein(diaryEntryWithMeals.meals)
        fatConsumed = diaryEntryWithMeals.calculateTotalFat(diaryEntryWithMeals.meals)
        carbsConsumed = diaryEntryWithMeals.calculateTotalCarbs(diaryEntryWithMeals.meals)
        caloriesPercentage = (caloriesConsumed / user.dailyKcalIntake) * 100
        proteinPercentage = (proteinConsumed / user.dailyProteinIntakeInG) * 100
        fatPercentage = (fatConsumed / user.dailyFatIntakeInG) * 100
        carbsPercentage = (carbsConsumed / user.dailyCarbsIntakeInG) * 100
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

    fun updateUserToPreviousDay() {
        currentDay = currentDay.minusDays(1) // subtract 1 day from current day
        val user = ldUser.value
        if (user != null) {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDayOfYear = currentDay.dayOfYear
            user.currentDayOfMonth = currentDay.dayOfMonth
            user.currentDayOfWeek = currentDay.dayOfWeek.value
            viewModelScope.launch {
                userRepository.updateUser(user)
            }
        }
    }

    fun updateUserToCurrentDay() {
        currentDay = LocalDate.now()
        val user = ldUser.value
        if (user != null) {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDayOfYear = currentDay.dayOfYear
            user.currentDayOfMonth = currentDay.dayOfMonth
            user.currentDayOfWeek = currentDay.dayOfWeek.value
            viewModelScope.launch {
                userRepository.updateUser(user)
            }
        }
    }

    fun updateUserToNextDay() {
        currentDay = currentDay.plusDays(1)
        val user = ldUser.value
        if (user != null) {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDayOfYear = currentDay.dayOfYear
            user.currentDayOfMonth = currentDay.dayOfMonth
            user.currentDayOfWeek = currentDay.dayOfWeek.value
            viewModelScope.launch {
                userRepository.updateUser(user)
            }
        }
    }

    fun deleteMealFromDiary(position: Int) {
//        realm.executeTransaction {
//            diaryEntry.meals.removeAt(position)
//            it.insertOrUpdate(diaryEntry)
//        }
    }
}