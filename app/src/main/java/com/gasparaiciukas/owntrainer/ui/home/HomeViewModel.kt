package com.gasparaiciukas.owntrainer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.model.NutrientType
import com.gasparaiciukas.owntrainer.model.StatisticsItem
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntry
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryMealCrossRef
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryWithMeals
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.database.User
import com.gasparaiciukas.owntrainer.utils.network.Resource
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.utils.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val allMeals = diaryRepository.getAllMealsWithFoodEntries()
    private val user = userRepository.user
    private val diaryEntryWithMeals: Flow<DiaryEntryWithMeals?> = user.flatMapLatest { u ->
        diaryRepository.getDiaryEntryWithMeals(u.year, u.dayOfYear)
    }

    val uiState: StateFlow<Resource<HomeUiState>?> =
        combine(allMeals, user, diaryEntryWithMeals) { lAllMeals, lUser, lDiaryEntryWithMeals ->
            if (lDiaryEntryWithMeals == null) {
                createDiaryEntry(lUser)
                return@combine Resource.loading(null)
            }
            val meals = mutableListOf<MealWithFoodEntries>()
            var caloriesConsumed = 0.0
            var proteinConsumed = 0.0
            var fatConsumed = 0.0
            var carbsConsumed = 0.0
            for (meal in lDiaryEntryWithMeals.meals) {
                val mealWithFoodEntries =
                    diaryRepository.getMealWithFoodEntriesById(meal.mealId)
                mealWithFoodEntries?.let {
                    it.meal.calories = it.calories
                    caloriesConsumed += it.calories
                    it.meal.protein = it.protein
                    proteinConsumed += it.protein
                    it.meal.carbs = it.carbs
                    carbsConsumed += it.carbs
                    it.meal.fat = it.fat
                    fatConsumed += it.fat
                    meals.add(it)
                }
            }
            return@combine Resource.success(
                HomeUiState(
                    allMeals = lAllMeals,
                    meals = meals,
                    user = lUser,
                    diaryEntryWithMeals = lDiaryEntryWithMeals,
                    proteinItem = StatisticsItem(
                        type = NutrientType.PROTEIN,
                        title = R.string.protein,
                        percentage = ((proteinConsumed / lUser.dailyProteinIntakeInG) * 100).toString(),
                        value = proteinConsumed.toString(),
                    ),
                    fatItem = StatisticsItem(
                        type = NutrientType.FAT,
                        title = R.string.fat,
                        percentage = ((fatConsumed / lUser.dailyFatIntakeInG) * 100).toString(),
                        value = fatConsumed.toString(),
                    ),
                    carbsItem = StatisticsItem(
                        type = NutrientType.CARBS,
                        title = R.string.carbs,
                        percentage = ((carbsConsumed / lUser.dailyCarbsIntakeInG) * 100).toString(),
                        value = carbsConsumed.toString(),
                    ),
                    energyItem = StatisticsItem(
                        type = NutrientType.ENERGY,
                        title = R.string.calories,
                        percentage = ((caloriesConsumed / lUser.dailyKcalIntake) * 100).toString(),
                        value = caloriesConsumed.toString(),
                    ),
                ),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null),
        )

    private suspend fun createDiaryEntry(user: User) {
        // Current day's entry does not exist - insert it to the database
        val currentDay = LocalDate.ofYearDay(user.year, user.dayOfYear)
        val diaryEntry = DiaryEntry(
            year = currentDay.year,
            dayOfYear = currentDay.dayOfYear,
            dayOfWeek = currentDay.dayOfWeek.value,
            monthOfYear = currentDay.monthValue,
            dayOfMonth = currentDay.dayOfMonth,
        )
        diaryRepository.insertDiaryEntry(diaryEntry)
    }

    fun updateUserToPreviousDay() {
        uiState.value?.data?.user?.let {
            val currentDay = LocalDate.ofYearDay(it.year, it.dayOfYear)
            updateUserDate(currentDay.minusDays(1))
        }
    }

    fun updateUserToCurrentDay() {
        updateUserDate(LocalDate.now())
    }

    fun updateUserToNextDay() {
        uiState.value?.data?.user?.let {
            val currentDay = LocalDate.ofYearDay(it.year, it.dayOfYear)
            updateUserDate(currentDay.plusDays(1))
        }
    }

    private fun updateUserDate(currentDay: LocalDate) {
        viewModelScope.launch {
            uiState.value?.data?.user?.let {
                it.year = currentDay.year
                it.month = currentDay.monthValue
                it.dayOfYear = currentDay.dayOfYear
                it.dayOfMonth = currentDay.dayOfMonth
                it.dayOfWeek = currentDay.dayOfWeek.value
                userRepository.updateUser(it)
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
            uiState.value?.data?.diaryEntryWithMeals?.let { diaryEntryWithMeals ->
                diaryRepository.insertDiaryEntryMealCrossRef(
                    DiaryEntryMealCrossRef(
                        diaryEntryWithMeals.diaryEntry.diaryEntryId,
                        mealWithFoodEntries.meal.mealId,
                    ),
                )
            }
        }
    }
}
