package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

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
    fun `when calculateData() is called, should calculate data correctly`() {
        runTest {
            // Testing data
            val meals = mutableListOf(
                Meal("Agirdi", "Nuklausau"),
                Meal("Abalys", "Prakandys"),
                Meal("Ananas", "Rakatas")
            )
            meals[0].mealId = 1
            meals[1].mealId = 2
            meals[2].mealId = 3

            diaryRepository.setFoodEntries(
                mutableListOf(
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
                )
            )
            diaryRepository.addMeal(meals[0])
            diaryRepository.setFoodEntries(
                mutableListOf(
                    FoodEntry(
                        mealId = 2,
                        title = "Tofu",
                        caloriesPer100G = 76.0,
                        carbsPer100G = 1.9,
                        fatPer100G = 4.8,
                        proteinPer100G = 8.0,
                        quantityInG = 150.0
                    )
                )
            )
            diaryRepository.addMeal(meals[1])
            diaryRepository.setFoodEntries(
                mutableListOf(
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
            diaryRepository.addMeal(meals[2])
            diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 1))
            diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 1))
            diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 2))
            diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 3))
            diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 3))

            val date = LocalDate.of(2022, 7, 19)
            val diaryEntry = DiaryEntry(
                year = date.year,
                dayOfYear = date.dayOfYear,
                dayOfWeek = date.dayOfWeek.value,
                monthOfYear = date.monthValue,
                dayOfMonth = date.dayOfMonth
            )
            diaryEntry.diaryEntryId = 1
            diaryRepository.insertDiaryEntry(diaryEntry)

            // Perform calculations
            viewModel.user = userRepository.user.asLiveData().getOrAwaitValueTest()
            viewModel.diaryEntryWithMeals = diaryRepository.getDiaryEntryWithMeals(
                date.year,
                date.dayOfYear
            ).asLiveData().getOrAwaitValueTest()
            viewModel.calculateData()

            // Assertions
            assertThat(viewModel.caloriesConsumed).isWithin(1.0e-10).of(
                ((89.0 * 120.0 / 100) + (155.0 * 60.0 / 100)) +
                        (76.0 * 150.0 / 100) +
                        (172.0 * 500.0 / 100) + (365.0 * 200.0 / 100)
            )
            assertThat(viewModel.proteinConsumed).isWithin(1.0e-10).of(
                ((1.1 * 120.0 / 100) + (13.0 * 60.0 / 100)) +
                        (8.0 * 150.0 / 100) +
                        (21.0 * 500.0 / 100) + (7.0 * 200.0 / 100)
            )
            assertThat(viewModel.fatConsumed).isWithin(1.0e-10).of(
                ((0.3 * 120.0 / 100) + (11.0 * 60.0 / 100)) +
                        (4.8 * 150.0 / 100) +
                        (9.0 * 500.0 / 100) + (0.7 * 200.0 / 100)
            )
            assertThat(viewModel.carbsConsumed).isWithin(1.0e-10).of(
                ((23.0 * 120.0 / 100) + (1.1 * 60.0 / 100)) +
                        (1.9 * 150.0 / 100) +
                        (0.0 * 500.0 / 100) + (80.0 * 200.0 / 100)
            )
        }
    }

    @Test
    fun `when createDiaryEntry() is called, should create diary entry`() = runTest {
        viewModel.currentDay = LocalDate.of(2022, 7, 19)
        val diaryEntry = DiaryEntry(
            year = viewModel.currentDay.year,
            dayOfYear = viewModel.currentDay.dayOfYear,
            dayOfWeek = viewModel.currentDay.dayOfWeek.value,
            monthOfYear = viewModel.currentDay.monthValue,
            dayOfMonth = viewModel.currentDay.dayOfMonth
        )
        viewModel.createDiaryEntry()
        assertThat(
            diaryRepository.getDiaryEntry(
                viewModel.currentDay.year,
                viewModel.currentDay.dayOfYear
            ).asLiveData().getOrAwaitValueTest()
        ).isEqualTo(diaryEntry)
    }

    @Test
    fun `when updateUserToPreviousDay() is called, should update user to previous day`() = runTest {
        var date = LocalDate.of(2022, 7, 19)
        viewModel.currentDay = date
        viewModel.user = User(
                sex = "Male",
                ageInYears = 25,
                heightInCm = 180,
                weightInKg = 80.0,
                lifestyle = "Lightly active",
                year = viewModel.currentDay.year,
                month = viewModel.currentDay.monthValue,
                dayOfYear = viewModel.currentDay.dayOfYear,
                dayOfMonth = viewModel.currentDay.dayOfMonth,
                dayOfWeek = viewModel.currentDay.dayOfWeek.value
        )
        viewModel.updateUserToPreviousDay()
        val user = userRepository.user.asLiveData().getOrAwaitValueTest()
        val userDate = LocalDate.ofYearDay(user.year, user.dayOfYear)
        date = date.minusDays(1)
        assertThat(userDate).isEqualTo(date)
    }

    @Test
    fun `when updateUserToCurrentDay() is called, should update user to current day`() = runTest {
        var date = LocalDate.of(2022, 7, 19)
        viewModel.currentDay = date
        viewModel.user = User(
            sex = "Male",
            ageInYears = 25,
            heightInCm = 180,
            weightInKg = 80.0,
            lifestyle = "Lightly active",
            year = viewModel.currentDay.year,
            month = viewModel.currentDay.monthValue,
            dayOfYear = viewModel.currentDay.dayOfYear,
            dayOfMonth = viewModel.currentDay.dayOfMonth,
            dayOfWeek = viewModel.currentDay.dayOfWeek.value
        )
        viewModel.updateUserToCurrentDay()
        val user = userRepository.user.asLiveData().getOrAwaitValueTest()
        val userDate = LocalDate.ofYearDay(user.year, user.dayOfYear)
        date = LocalDate.now()
        assertThat(userDate).isEqualTo(date)
    }

    @Test
    fun `when updateUserToNextDay() is called, should update user to next day`() = runTest {
        var date = LocalDate.of(2022, 7, 19)
        viewModel.currentDay = date
        viewModel.user = User(
            sex = "Male",
            ageInYears = 25,
            heightInCm = 180,
            weightInKg = 80.0,
            lifestyle = "Lightly active",
            year = viewModel.currentDay.year,
            month = viewModel.currentDay.monthValue,
            dayOfYear = viewModel.currentDay.dayOfYear,
            dayOfMonth = viewModel.currentDay.dayOfMonth,
            dayOfWeek = viewModel.currentDay.dayOfWeek.value
        )
        viewModel.updateUserToNextDay()
        val user = userRepository.user.asLiveData().getOrAwaitValueTest()
        val userDate = LocalDate.ofYearDay(user.year, user.dayOfYear)
        date = date.plusDays(1)
        assertThat(userDate).isEqualTo(date)
    }

    @Test
    fun `when deleteMealFromDiary() is called, should delete meal from diary`() = runTest {
        val diaryEntry = DiaryEntry(1, 1, 1, 1, 1)
        diaryEntry.diaryEntryId = 39
        val meal = Meal("Egg", "Put in pan")
        meal.mealId = 39
        val crossRef = DiaryEntryMealCrossRef(
            diaryEntry.diaryEntryId,
            meal.mealId
        )
        diaryRepository.insertDiaryEntry(diaryEntry)
        diaryRepository.addMeal(meal)
        diaryRepository.insertDiaryEntryMealCrossRef(crossRef)
        viewModel.deleteMealFromDiary(diaryEntry.diaryEntryId, meal.mealId)
        val diaryEntryWithMeals = diaryRepository.getDiaryEntryWithMeals(1, 1).asLiveData().getOrAwaitValueTest()

        assertThat(diaryEntryWithMeals.meals).doesNotContain(meal)
    }
}