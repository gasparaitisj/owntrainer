package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MealItemViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MealItemViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var userRepository: FakeUserRepository
    private lateinit var diaryRepository: FakeDiaryRepository

    @Before
    fun setup() {
        userRepository = FakeUserRepository()
        diaryRepository = FakeDiaryRepository()
        savedStateHandle = SavedStateHandle().apply {
            set("diaryEntryId", 1)
            set("mealId", 1)
        }
        viewModel = MealItemViewModel(userRepository, diaryRepository, savedStateHandle)
    }

    @Test
    fun `when loadData() is called, should load data`() = runTest {
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        val foodEntry1 = FoodEntry(
            mealId = 1,
            title = "Banana",
            caloriesPer100G = 89.0,
            carbsPer100G = 23.0,
            fatPer100G = 0.3,
            proteinPer100G = 1.1,
            quantityInG = 120.0
        )
        val foodEntry2 = FoodEntry(
            mealId = 2,
            title = "Egg",
            caloriesPer100G = 155.0,
            carbsPer100G = 1.1,
            fatPer100G = 11.0,
            proteinPer100G = 13.0,
            quantityInG = 60.0
        )
        val mealWithFoodEntries = MealWithFoodEntries(
            meal,
            listOf(
                foodEntry1,
                foodEntry2
            )
        )
        diaryRepository.insertMeal(meal)
        diaryRepository.insertFood(foodEntry1)
        diaryRepository.insertFood(foodEntry2)
        val user = viewModel.ldUser.getOrAwaitValueTest()
        viewModel.loadData(user)
        val sum = mealWithFoodEntries.meal.carbs +
                mealWithFoodEntries.meal.fat +
                mealWithFoodEntries.meal.protein
        val uiState = viewModel.uiState.getOrAwaitValueTest()
        val uiStateTest = MealItemUiState(
            user = user,
            mealWithFoodEntries = mealWithFoodEntries,
            carbsPercentage = mealWithFoodEntries.meal.carbs / sum * 100,
            fatPercentage = mealWithFoodEntries.meal.fat / sum * 100,
            proteinPercentage = mealWithFoodEntries.meal.protein / sum * 100,
            carbsDailyIntakePercentage = mealWithFoodEntries.meal.carbs / user.dailyCarbsIntakeInG * 100,
            fatDailyIntakePercentage = mealWithFoodEntries.meal.fat / user.dailyFatIntakeInG * 100,
            proteinDailyIntakePercentage = mealWithFoodEntries.meal.protein / user.dailyProteinIntakeInG * 100,
            caloriesDailyIntakePercentage = mealWithFoodEntries.meal.calories / user.dailyKcalIntake * 100
        )
        assertThat(uiState).isEqualTo(uiStateTest)
    }

    @Test
    fun `when deleteFoodFromMeal() is called, should delete food from meal`() = runTest {
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        val foodEntry1 = FoodEntry(
            mealId = 1,
            title = "Banana",
            caloriesPer100G = 89.0,
            carbsPer100G = 23.0,
            fatPer100G = 0.3,
            proteinPer100G = 1.1,
            quantityInG = 120.0
        )
        val foodEntry2 = FoodEntry(
            mealId = 2,
            title = "Egg",
            caloriesPer100G = 155.0,
            carbsPer100G = 1.1,
            fatPer100G = 11.0,
            proteinPer100G = 13.0,
            quantityInG = 60.0
        )
        diaryRepository.insertMeal(meal)
        diaryRepository.insertFood(foodEntry1)
        diaryRepository.insertFood(foodEntry2)

        viewModel.deleteFoodFromMeal(foodEntry1.foodEntryId)

        val foodEntries = diaryRepository.getAllFoodEntries().asLiveData().getOrAwaitValueTest()
        assertThat(foodEntries).doesNotContain(foodEntry1)
    }
}