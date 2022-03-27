package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddFoodToMealViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddFoodToMealViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var diaryRepository: FakeDiaryRepository

    @Before
    fun setup() {
        diaryRepository = FakeDiaryRepository()
        savedStateHandle = SavedStateHandle().apply {
            set("foodItem", createFood())
            set("quantity", 150)
        }
        viewModel = AddFoodToMealViewModel(diaryRepository, savedStateHandle)
    }

    @Test
    fun `when addFoodToMeal() is called, should add food to meal`() = runTest {
        val quantity: Int? = savedStateHandle["quantity"]
        val foodEntry = FoodEntry(
            mealId = 1,
            title = "APPLE",
            caloriesPer100G = 52.0,
            carbsPer100G = 14.3,
            fatPer100G = 0.65,
            proteinPer100G = 0.0,
            quantityInG = quantity?.toDouble() ?: 0.0
        )
        val meal = Meal(
            mealId = 1,
            title = "Apple pie",
            instructions = "Put in oven"
        )
        diaryRepository.insertMeal(meal)
        val mealWithFoodEntries = MealWithFoodEntries(
            meal,
            listOf(foodEntry)
        )
        viewModel.addFoodToMeal(MealWithFoodEntries(meal, listOf()))
        assertThat(viewModel.ldMeals.getOrAwaitValueTest()).contains(mealWithFoodEntries)
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