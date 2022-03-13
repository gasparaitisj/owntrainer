package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
            set("mealId", 1)
        }
        viewModel = MealItemViewModel(userRepository, diaryRepository, savedStateHandle)
    }

    @Test
    fun `when loadData() is called, should load data`() = runTest {
        viewModel.user = userRepository.user.asLiveData().getOrAwaitValueTest()
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
        viewModel.loadData()
        val sum = (((23.0 * 120.0 / 100.0) + (1.1 * 60.0 / 100.0))) +
                (((0.3 * 120.0 / 100.0) + (11.0 * 60.0 / 100.0))) +
                (((1.1 * 120.0 / 100.0) + (13.0 * 60.0 / 100.0)))
        assertThat(viewModel.carbsPercentage).isEqualTo(((((23.0 * 120.0 / 100.0) + (1.1 * 60.0 / 100.0))) / sum * 100).toFloat())
        assertThat(viewModel.fatPercentage).isEqualTo(((((0.3 * 120.0 / 100.0) + (11.0 * 60.0 / 100.0))) / sum * 100).toFloat())
        assertThat(viewModel.proteinPercentage).isEqualTo(((((1.1 * 120.0 / 100.0) + (13.0 * 60.0 / 100.0))) / sum * 100).toFloat())
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