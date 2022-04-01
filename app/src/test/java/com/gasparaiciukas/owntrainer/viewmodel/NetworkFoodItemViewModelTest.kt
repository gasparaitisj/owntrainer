package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NetworkFoodItemViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val foodItem = createFood()
    private lateinit var viewModel: NetworkFoodItemViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var userRepository: FakeUserRepository

    @Before
    fun setup() {
        userRepository = FakeUserRepository()
        savedStateHandle = SavedStateHandle().apply {
            set("foodItem", foodItem)
        }
        viewModel = NetworkFoodItemViewModel(userRepository, savedStateHandle)
    }

    @Test
    fun `when loadData() is called, should load data`() = runTest {
        viewModel.ldUser.getOrAwaitValueTest()
        viewModel.loadData()
        val uiState = viewModel.uiState.getOrAwaitValueTest()
        val sum = 0.0f + 0.65f + 14.3f
        val uiStateTest = NetworkFoodItemUiState(
            user = viewModel.ldUser.getOrAwaitValueTest(),
            foodItem = foodItem,
            title = foodItem.description.toString(),
            carbs = 14.3f,
            carbsPercentage = 14.3f / sum * 100,
            calories = 52.0f,
            fat = 0.65f,
            fatPercentage = 0.65f / sum * 100,
            protein = 0.0f,
            proteinPercentage = 0.0f / sum * 100
        )
        assertThat(uiState).isEqualTo(uiStateTest)
    }

    private fun createFood(): Food {
        return Food(
            fdcId = 454004,
            description = "APPLE",
            lowercaseDescription = "apple",
            dataType = "Branded",
            gtinUpc = "867824000001",
            publishedDate =  "2019-04-01",
            brandOwner = "TREECRISP 2 GO",
            ingredients = "CRISP APPLE.",
            marketCountry = "United States",
            foodCategory = "Pre-Packaged Fruit & Vegetables",
            allHighlightFields = "<b>Ingredients</b>: CRISP <em>APPLE</em>.",
            score = 932.4247,
            foodNutrients = listOf(
                FoodNutrient(
                    nutrientId = 1003,
                    nutrientName =  "Protein",
                    nutrientNumber = "203",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 0.0
                ),
                FoodNutrient(
                    nutrientId = 1005,
                    nutrientName =  "Carbohydrate, by difference",
                    nutrientNumber = "205",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 14.3
                ),
                FoodNutrient(
                    nutrientId = 1008,
                    nutrientName =  "Energy",
                    nutrientNumber = "208",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 52.0
                ),
                FoodNutrient(
                    nutrientId = 1004,
                    nutrientName =  "Total lipid (fat)",
                    nutrientNumber = "204",
                    unitName = "G",
                    derivationCode = "LCSL",
                    derivationDescription = "Calculated from a less than value per serving size measure",
                    value = 0.65
                ),
            )
        )
    }
}