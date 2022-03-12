package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
        viewModel.user = userRepository.user.asLiveData().getOrAwaitValueTest()
        viewModel.loadData()
        val sum = (1.1 * 60.0 / 100.0) + (11.0 * 60.0 / 100.0) + (13.0 * 60.0 / 100.0)
        assertThat(viewModel.carbsPercentage).isEqualTo((1.1 * 60.0 / 100.0 / sum * 100).toFloat())
        assertThat(viewModel.fatPercentage).isEqualTo((11.0 * 60.0 / 100.0 / sum * 100).toFloat())
        assertThat(viewModel.proteinPercentage).isEqualTo((13.0 * 60.0 / 100.0 / sum * 100).toFloat())
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