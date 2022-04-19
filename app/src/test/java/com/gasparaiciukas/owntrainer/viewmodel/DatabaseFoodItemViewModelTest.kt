package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DatabaseFoodItemViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: DatabaseFoodItemViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var userRepository: FakeUserRepository
    private val foodEntry = createFoodEntry()

    @Before
    fun setup() {
        userRepository = FakeUserRepository()
        savedStateHandle = SavedStateHandle().apply {
            set("food", foodEntry)
        }
        viewModel = DatabaseFoodItemViewModel(userRepository, savedStateHandle)
    }

    @Test
    fun `when loadData() is called, should load data`() = runTest {
        val user = viewModel.user.first()
        val sum = foodEntry.carbs + foodEntry.protein + foodEntry.fat
        val uiStateTest = DatabaseFoodItemUiState(
            food = foodEntry,
            carbsPercentage = (foodEntry.carbs / sum * 100).toFloat(),
            carbsDailyIntake = user.dailyCarbsIntakeInG.toFloat(),
            carbsDailyIntakePercentage = (foodEntry.carbs / user.dailyCarbsIntakeInG * 100).toFloat(),
            caloriesDailyIntake = user.dailyKcalIntake.toFloat(),
            caloriesDailyIntakePercentage = (foodEntry.calories / user.dailyKcalIntake * 100).toFloat(),
            fatPercentage = (foodEntry.fat / sum * 100).toFloat(),
            fatDailyIntake = user.dailyFatIntakeInG.toFloat(),
            fatDailyIntakePercentage = (foodEntry.fat / user.dailyFatIntakeInG * 100).toFloat(),
            proteinPercentage = (foodEntry.protein / sum * 100).toFloat(),
            proteinDailyIntake = user.dailyProteinIntakeInG.toFloat(),
            proteinDailyIntakePercentage = (foodEntry.protein / user.dailyProteinIntakeInG * 100).toFloat(),
        )
        assertThat(viewModel.uiState.first().data).isEqualTo(uiStateTest)
    }

    private fun createFoodEntry(): FoodEntryParcelable {
        return FoodEntryParcelable(
            title = "Egg",
            caloriesPer100G = 155.0,
            calories = 155.0 * 60.0 / 100.0,
            carbsPer100G = 1.1,
            carbs = 1.1 * 60.0 / 100.0,
            fatPer100G = 11.0,
            fat = 11.0 * 60.0 / 100.0,
            proteinPer100G = 13.0,
            protein = 13.0 * 60.0 / 100.0,
            quantityInG = 60.0
        )
    }
}