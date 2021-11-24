package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.GetService
import io.realm.Realm
import timber.log.Timber
import java.time.LocalDate

class DiaryViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()
    private lateinit var currentDay: LocalDate

    lateinit var diaryEntry: DiaryEntry
    lateinit var user: User

    var caloriesConsumed: Double = 0.0
    var proteinConsumed: Double = 0.0
    var fatConsumed: Double = 0.0
    var carbsConsumed: Double = 0.0
    var caloriesPercentage: Double = 0.0
    var proteinPercentage: Double = 0.0
    var fatPercentage: Double = 0.0
    var carbsPercentage: Double = 0.0

    private val _dataChanged = MutableLiveData<Boolean>()
    val dataChanged: LiveData<Boolean>
        get() = _dataChanged

    init {
        realm.addChangeListener {
            loadData()
            _dataChanged.value = _dataChanged.value != true
            Timber.d("Data changed!")
        }
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    private fun loadData() {
        loadDiaryEntry()
        calculateData()
    }

    private fun calculateData() {
        caloriesConsumed = diaryEntry.calculateTotalCalories(diaryEntry.meals)
        proteinConsumed = diaryEntry.calculateTotalProtein(diaryEntry.meals)
        fatConsumed = diaryEntry.calculateTotalFat(diaryEntry.meals)
        carbsConsumed = diaryEntry.calculateTotalCarbs(diaryEntry.meals)
        caloriesPercentage = (caloriesConsumed / user.dailyKcalIntake) * 100
        proteinPercentage = (proteinConsumed / user.dailyProteinIntakeInG) * 100
        fatPercentage = (fatConsumed / user.dailyFatIntakeInG) * 100
        carbsPercentage = (carbsConsumed / user.dailyCarbsIntakeInG) * 100
    }

    private fun loadDiaryEntry() {
        // Get user from database
        user = realm.where(User::class.java).findFirst()!!

        val year = user.currentYear
        val month = user.currentMonth
        val day = user.currentDay
        currentDay = LocalDate.of(year, month, day)

        // Try to get diary entry from database
        val diaryEntryFromDatabase = realm.where(DiaryEntry::class.java)
            .equalTo("yearAndDayOfYear", currentDay.year.toString() + currentDay.dayOfYear)
            .findFirst()

        // If current day's entry does not exist, insert it to the database
        if (diaryEntryFromDatabase == null) {
            val newDiaryEntry = DiaryEntry()
            newDiaryEntry.yearAndDayOfYear = currentDay.year.toString() + currentDay.dayOfYear
            newDiaryEntry.year = currentDay.year
            newDiaryEntry.dayOfYear = currentDay.dayOfYear
            newDiaryEntry.dayOfMonth = currentDay.dayOfMonth
            newDiaryEntry.dayOfWeek = currentDay.dayOfWeek.value
            newDiaryEntry.monthOfYear = currentDay.monthValue
            realm.executeTransaction { it.insertOrUpdate(newDiaryEntry) }
            diaryEntry = newDiaryEntry
        } else {
            diaryEntry = diaryEntryFromDatabase
        }
    }

    fun updateUserToPreviousDay() {
        currentDay = currentDay.minusDays(1) // subtract 1 day from current day
        realm.executeTransaction {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDay = currentDay.dayOfMonth
            it.insertOrUpdate(user)
        }
    }

    fun updateUserToCurrentDay() {
        currentDay = LocalDate.now()
        realm.executeTransaction {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDay = currentDay.dayOfMonth
            it.insertOrUpdate(user)
        }
    }

    fun updateUserToNextDay() {
        currentDay = currentDay.plusDays(1)
        realm.executeTransaction {
            user.currentYear = currentDay.year
            user.currentMonth = currentDay.monthValue
            user.currentDay = currentDay.dayOfMonth
            it.insertOrUpdate(user)
        }
    }

    fun deleteMealFromDiary(position: Int) {
        realm.executeTransaction {
            diaryEntry.meals.removeAt(position)
            it.insertOrUpdate(diaryEntry)
        }
    }
}