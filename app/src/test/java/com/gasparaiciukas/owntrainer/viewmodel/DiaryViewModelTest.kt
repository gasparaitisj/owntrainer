package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
    fun `when loading UI state, should load UI state correctly`() = runTest {
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
        val user = viewModel.user.first()
        user.dayOfMonth = date.dayOfMonth
        user.dayOfWeek = date.dayOfWeek.value
        user.dayOfYear = date.dayOfYear
        user.year = date.year
        user.month = date.monthValue
        userRepository.updateUser(user)

        // Perform calculations
        val diaryEntryWithMeals = viewModel.diaryEntryWithMeals.first()
        val uiStateData = viewModel.uiState.first()?.data

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
            diaryEntryWithMeals = diaryEntryWithMeals!!,
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
        assertThat(uiStateData?.meals).isEqualTo(uiStateTest.meals)
        assertThat(uiStateData?.user).isEqualTo(uiStateTest.user)
        assertThat(uiStateData?.diaryEntryWithMeals).isEqualTo(uiStateTest.diaryEntryWithMeals)
        assertThat(uiStateData?.proteinConsumed).isEqualTo(uiStateTest.proteinConsumed)
        assertThat(uiStateData?.fatConsumed).isEqualTo(uiStateTest.fatConsumed)
        assertThat(uiStateData?.carbsConsumed).isEqualTo(uiStateTest.carbsConsumed)
        assertThat(uiStateData?.caloriesConsumed).isEqualTo(uiStateTest.caloriesConsumed)
        assertThat(uiStateData?.caloriesPercentage).isEqualTo(uiStateTest.caloriesPercentage)
        assertThat(uiStateData?.proteinPercentage).isEqualTo(uiStateTest.proteinPercentage)
        assertThat(uiStateData?.proteinConsumed).isEqualTo(uiStateTest.proteinConsumed)
        assertThat(uiStateData?.fatPercentage).isEqualTo(uiStateTest.fatPercentage)
        assertThat(uiStateData?.carbsPercentage).isEqualTo(uiStateTest.carbsPercentage)
    }

    @Test
    fun `when createDiaryEntry() is called, should create diary entry`() = runTest {
        val user = viewModel.user.first()
        val diaryEntry = DiaryEntry(
            year = user.year,
            dayOfYear = user.dayOfYear,
            dayOfWeek = user.dayOfWeek,
            monthOfYear = user.month,
            dayOfMonth = user.dayOfMonth
        )
        viewModel.createDiaryEntry(user)
        assertThat(
            diaryRepository.getDiaryEntry(
                user.year,
                user.dayOfYear
            ).first()
        ).isEqualTo(diaryEntry)
    }

    @Test
    fun `when updateUserToPreviousDay() is called, should update user to previous day`() = runTest {
        val user = viewModel.user.first().copy()
        viewModel.createDiaryEntry(user)
        viewModel.uiState.first()


        viewModel.updateUserToPreviousDay()
        val userTest = viewModel.user.first().copy()
        assertThat(LocalDate.ofYearDay(user.year, user.dayOfYear).minusDays(1))
            .isEqualTo(LocalDate.ofYearDay(userTest.year, userTest.dayOfYear))
    }

    @Test
    fun `when updateUserToCurrentDay() is called, should update user to current day`() = runTest {
        val user = viewModel.user.first().copy()
        viewModel.createDiaryEntry(user)

        viewModel.updateUserToCurrentDay()
        val userTest = viewModel.user.first().copy()
        assertThat(LocalDate.now())
            .isEqualTo(LocalDate.ofYearDay(userTest.year, userTest.dayOfYear))
    }

    @Test
    fun `when updateUserToNextDay() is called, should update user to next day`() = runTest {
        val user = viewModel.user.first().copy()
        viewModel.createDiaryEntry(user)
        viewModel.uiState.first()

        viewModel.updateUserToNextDay()
        val userTest = viewModel.user.first().copy()
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
            diaryRepository.getDiaryEntryWithMeals(1, 1).first()

        assertThat(diaryEntryWithMeals?.meals).doesNotContain(meal)
    }

    @Test
    fun `when addMealToDiary() is called, should add meal to diary`() = runTest {
        val meal = Meal(
            mealId = 1,
            title = "Apple pie",
            instructions = "Put in oven"
        )
        val diaryEntry = DiaryEntry(
            year = 2021,
            dayOfYear = 1,
            dayOfWeek = 1,
            monthOfYear = 1,
            dayOfMonth = 1
        )
        diaryRepository.insertDiaryEntry(diaryEntry)
        diaryRepository.insertMeal(meal)

        val user = viewModel.user.first()
        user.year = diaryEntry.year
        user.dayOfYear = diaryEntry.dayOfYear
        userRepository.updateUser(user)

        viewModel.uiState.first()
        viewModel.addMealToDiary(
            MealWithFoodEntries(
                meal,
                listOf()
            )
        )

        val diaryEntryWithMeals = diaryRepository.getDiaryEntryWithMeals(
            year = 2021,
            dayOfYear = 1
        ).first()

        assertThat(diaryEntryWithMeals?.meals).contains(meal)
    }
}