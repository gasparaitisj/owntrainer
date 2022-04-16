package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class DiaryViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: DiaryViewModel
    private lateinit var userRepository: FakeUserRepository
    private lateinit var diaryRepository: FakeDiaryRepository

    @Before
    fun setup() {
        userRepository = FakeUserRepository()
        diaryRepository = FakeDiaryRepository()
        viewModel = DiaryViewModel(userRepository, diaryRepository)
    }

    @Test
    fun `when calculateData() is called, should calculate data correctly`() = runTest {
        // Testing data
        val meals = listOf(
            Meal(1, "Agirdi", "Nuklausau"),
            Meal(2, "Abalys", "Prakandys"),
            Meal(3, "Ananas", "Rakatas")
        )

        val foodEntries = listOf(
            listOf(
                FoodEntry(
                    mealId = 1,
                    title = "Banana",
                    caloriesPer100G = 89.0,
                    carbsPer100G = 23.0,
                    fatPer100G = 0.3,
                    proteinPer100G = 1.1,
                    quantityInG = 120.0
                ),
                FoodEntry(
                    mealId = 1,
                    title = "Egg",
                    caloriesPer100G = 155.0,
                    carbsPer100G = 1.1,
                    fatPer100G = 11.0,
                    proteinPer100G = 13.0,
                    quantityInG = 60.0
                )
            ),
            listOf(
                FoodEntry(
                    mealId = 2,
                    title = "Tofu",
                    caloriesPer100G = 76.0,
                    carbsPer100G = 1.9,
                    fatPer100G = 4.8,
                    proteinPer100G = 8.0,
                    quantityInG = 150.0
                )
            ),
            listOf(
                FoodEntry(
                    mealId = 3,
                    title = "Chicken breast",
                    caloriesPer100G = 172.0,
                    carbsPer100G = 0.0,
                    fatPer100G = 9.0,
                    proteinPer100G = 21.0,
                    quantityInG = 500.0
                ),
                FoodEntry(
                    mealId = 3,
                    title = "White rice, raw",
                    caloriesPer100G = 365.0,
                    carbsPer100G = 80.0,
                    fatPer100G = 0.7,
                    proteinPer100G = 7.0,
                    quantityInG = 200.0
                )
            )
        )

        val mealsWithFoodEntries = listOf(
            MealWithFoodEntries(meals[0], foodEntries[0] + foodEntries[1] + foodEntries[2]),
            MealWithFoodEntries(meals[0], foodEntries[0] + foodEntries[1] + foodEntries[2]),
            MealWithFoodEntries(meals[1], foodEntries[0] + foodEntries[1] + foodEntries[2]),
            MealWithFoodEntries(meals[2], foodEntries[0] + foodEntries[1] + foodEntries[2]),
            MealWithFoodEntries(meals[2], foodEntries[0] + foodEntries[1] + foodEntries[2])
        )

        diaryRepository.insertMeal(meals[0])
        diaryRepository.insertMeal(meals[0])
        diaryRepository.insertMeal(meals[1])
        diaryRepository.insertMeal(meals[2])
        diaryRepository.insertMeal(meals[2])

        diaryRepository.insertFood(foodEntries[0][0])
        diaryRepository.insertFood(foodEntries[0][1])
        diaryRepository.insertFood(foodEntries[1][0])
        diaryRepository.insertFood(foodEntries[2][0])
        diaryRepository.insertFood(foodEntries[2][1])

        diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 1))
        diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 1))
        diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 2))
        diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 3))
        diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 3))

        val date = LocalDate.of(2022, 7, 19)
        val diaryEntry = DiaryEntry(
            diaryEntryId = 1,
            year = date.year,
            dayOfYear = date.dayOfYear,
            dayOfWeek = date.dayOfWeek.value,
            monthOfYear = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )
        diaryRepository.insertDiaryEntry(diaryEntry)
        val user = viewModel.ldUser.getOrAwaitValueTest()
        user.dayOfMonth = date.dayOfMonth
        user.dayOfWeek = date.dayOfWeek.value
        user.dayOfYear = date.dayOfYear
        user.year = date.year
        user.month = date.monthValue
        userRepository.updateUser(user)

        // Perform calculations
        val diaryEntryWithMeals = viewModel.ldDiaryEntryWithMeals.getOrAwaitValueTest()
        viewModel.calculateData()
        val uiState = viewModel.uiState.getOrAwaitValueTest()

        val proteinConsumed = mealsWithFoodEntries[0].protein +
                mealsWithFoodEntries[0].protein +
                mealsWithFoodEntries[1].protein +
                mealsWithFoodEntries[2].protein +
                mealsWithFoodEntries[2].protein

        val fatConsumed = mealsWithFoodEntries[0].fat +
                mealsWithFoodEntries[0].fat +
                mealsWithFoodEntries[1].fat +
                mealsWithFoodEntries[2].fat +
                mealsWithFoodEntries[2].fat

        val carbsConsumed = mealsWithFoodEntries[0].carbs +
                mealsWithFoodEntries[0].carbs +
                mealsWithFoodEntries[1].carbs +
                mealsWithFoodEntries[2].carbs +
                mealsWithFoodEntries[2].carbs

        val caloriesConsumed = mealsWithFoodEntries[0].calories +
                mealsWithFoodEntries[0].calories +
                mealsWithFoodEntries[1].calories +
                mealsWithFoodEntries[2].calories +
                mealsWithFoodEntries[2].calories

        val uiStateTest = DiaryUiState(
            meals = mealsWithFoodEntries,
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
        // Assertions
        assertThat(uiState.meals).isEqualTo(uiStateTest.meals)
        assertThat(uiState.user).isEqualTo(uiStateTest.user)
        assertThat(uiState.diaryEntryWithMeals).isEqualTo(uiStateTest.diaryEntryWithMeals)
        assertThat(uiState.proteinConsumed).isEqualTo(uiStateTest.proteinConsumed)
        assertThat(uiState.fatConsumed).isEqualTo(uiStateTest.fatConsumed)
        assertThat(uiState.carbsConsumed).isEqualTo(uiStateTest.carbsConsumed)
        assertThat(uiState.caloriesConsumed).isEqualTo(uiStateTest.caloriesConsumed)
        assertThat(uiState.caloriesPercentage).isEqualTo(uiStateTest.caloriesPercentage)
        assertThat(uiState.proteinPercentage).isEqualTo(uiStateTest.proteinPercentage)
        assertThat(uiState.proteinConsumed).isEqualTo(uiStateTest.proteinConsumed)
        assertThat(uiState.fatPercentage).isEqualTo(uiStateTest.fatPercentage)
        assertThat(uiState.carbsPercentage).isEqualTo(uiStateTest.carbsPercentage)
    }

    @Test
    fun `when createDiaryEntry() is called, should create diary entry`() = runTest {
        val user = viewModel.ldUser.getOrAwaitValueTest()
        val diaryEntry = DiaryEntry(
            year = user.year,
            dayOfYear = user.dayOfYear,
            dayOfWeek = user.dayOfWeek,
            monthOfYear = user.month,
            dayOfMonth = user.dayOfMonth
        )
        viewModel.createDiaryEntry()
        assertThat(
            diaryRepository.getDiaryEntry(
                user.year,
                user.dayOfYear
            ).asLiveData().getOrAwaitValueTest()
        ).isEqualTo(diaryEntry)
    }

    @Test
    fun `when updateUserToPreviousDay() is called, should update user to previous day`() = runTest {
        val user = viewModel.ldUser.getOrAwaitValueTest().copy()
        viewModel.updateUserToPreviousDay()
        val userTest = viewModel.ldUser.getOrAwaitValueTest().copy()
        assertThat(LocalDate.ofYearDay(user.year, user.dayOfYear).minusDays(1))
            .isEqualTo(LocalDate.ofYearDay(userTest.year, userTest.dayOfYear))
    }

    @Test
    fun `when updateUserToCurrentDay() is called, should update user to current day`() = runTest {
        viewModel.updateUserToCurrentDay()
        val userTest = viewModel.ldUser.getOrAwaitValueTest().copy()
        assertThat(LocalDate.now())
            .isEqualTo(LocalDate.ofYearDay(userTest.year, userTest.dayOfYear))
    }

    @Test
    fun `when updateUserToNextDay() is called, should update user to next day`() = runTest {
        val user = viewModel.ldUser.getOrAwaitValueTest().copy()
        viewModel.updateUserToNextDay()
        val userTest = viewModel.ldUser.getOrAwaitValueTest().copy()
        assertThat(LocalDate.ofYearDay(user.year, user.dayOfYear).plusDays(1))
            .isEqualTo(LocalDate.ofYearDay(userTest.year, userTest.dayOfYear))
    }

    @Test
    fun `when deleteMealFromDiary() is called, should delete meal from diary`() = runTest {
        val diaryEntry = DiaryEntry(
            diaryEntryId = 39,
            year = 1,
            dayOfYear = 1,
            dayOfWeek = 1,
            monthOfYear = 1,
            dayOfMonth = 1
        )
        val meal = Meal(
            mealId = 39,
            title = "Egg",
            instructions = "Put in pan"
        )
        val crossRef = DiaryEntryMealCrossRef(
            diaryEntry.diaryEntryId,
            meal.mealId
        )
        diaryRepository.insertDiaryEntry(diaryEntry)
        diaryRepository.insertMeal(meal)
        diaryRepository.insertDiaryEntryMealCrossRef(crossRef)
        viewModel.deleteMealFromDiary(diaryEntry.diaryEntryId, meal.mealId)
        val diaryEntryWithMeals =
            diaryRepository.getDiaryEntryWithMeals(1, 1).asLiveData().getOrAwaitValueTest()

        assertThat(diaryEntryWithMeals.meals).doesNotContain(meal)
    }
}