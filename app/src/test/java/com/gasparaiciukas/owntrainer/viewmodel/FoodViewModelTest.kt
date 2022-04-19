package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FoodViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: FoodViewModel
    private lateinit var diaryRepository: FakeDiaryRepository
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        diaryRepository = FakeDiaryRepository()
        userRepository = FakeUserRepository()
        viewModel = FoodViewModel(diaryRepository, userRepository)
    }

    @Test
    fun `when getFoods() is called successfully, should get foods`() = runTest {
        diaryRepository.setShouldReturnNetworkError(false)
        viewModel.getFoods("test")
        val response = viewModel.response.first()
        assertThat(response.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `when getFoods() is called unsuccessfully, should return error`() = runTest {
        diaryRepository.setShouldReturnNetworkError(true)
        viewModel.getFoods("test")
        val response = viewModel.response.first()
        assertThat(response.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `when loadNetworkFoodItemUiState() is called, should load UI state`() = runTest {
        viewModel.foodItem = createFood()

        val uiStateData = viewModel.networkFoodItemUiState.first()?.data
        val sum = 0.0f + 0.65f + 14.3f
        val uiStateTest = NetworkFoodItemUiState(
            user = viewModel.user.first(),
            foodItem = viewModel.foodItem!!,
            title = viewModel.foodItem!!.description.toString(),
            carbs = 14.3f,
            carbsPercentage = 14.3f / sum * 100,
            calories = 52.0f,
            fat = 0.65f,
            fatPercentage = 0.65f / sum * 100,
            protein = 0.0f,
            proteinPercentage = 0.0f / sum * 100
        )
        assertThat(uiStateData).isEqualTo(uiStateTest)
    }

    @Test
    fun `when addFoodToMeal() is called, should add food to meal`() = runTest {
        val quantity = 100
        val foodEntry = FoodEntry(
            mealId = 1,
            title = "APPLE",
            caloriesPer100G = 52.0,
            carbsPer100G = 14.3,
            fatPer100G = 0.65,
            proteinPer100G = 0.0,
            quantityInG = quantity.toDouble()
        )
        val meal = Meal(
            mealId = 1,
            title = "Apple pie",
            instructions = "Put in oven"
        )
        diaryRepository.insertMeal(meal)
        diaryRepository.insertFood(foodEntry)
        val mealWithFoodEntries = MealWithFoodEntries(
            meal,
            listOf(foodEntry)
        )
        viewModel.addFoodToMeal(MealWithFoodEntries(meal, listOf()))
        assertThat(viewModel.meals.first()).contains(mealWithFoodEntries)
    }

    private fun createFood(): Food {
        return Food(
            fdcId = 454004,
            description = "APPLE",
            lowercaseDescription = "apple",
            dataType = "Branded",
            gtinUpc = "867824000001",
            publishedDate = "2019-04-01",
            brandOwner = "TREECRISP 2 GO",
            ingredients = "CRISP APPLE.",
            marketCountry = "United States",
            foodCategory = "Pre-Packaged Fruit & Vegetables",
            allHighlightFields = "<b>Ingredients</b>: CRISP <em>APPLE</em>.",
            score = 932.4247,
            foodNutrients = listOf(
                FoodNutrient(
                    nutrientId = 1003,
                    nutrientName = "Protein",
                    nutrientNumber = "203",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 0.0
                ),
                FoodNutrient(
                    nutrientId = 1005,
                    nutrientName = "Carbohydrate, by difference",
                    nutrientNumber = "205",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 14.3
                ),
                FoodNutrient(
                    nutrientId = 1008,
                    nutrientName = "Energy",
                    nutrientNumber = "208",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 52.0
                ),
                FoodNutrient(
                    nutrientId = 1004,
                    nutrientName = "Total lipid (fat)",
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