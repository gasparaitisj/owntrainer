package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MealViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MealViewModel
    private lateinit var diaryRepository: FakeDiaryRepository

    @Before
    fun setup() {
        diaryRepository = FakeDiaryRepository()
        viewModel = MealViewModel(diaryRepository)
    }

    @Test
    fun `when deleteMeal() is called, should delete meal`() = runTest {
        val meal1 = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        val meal2 = Meal(
            mealId = 2,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        diaryRepository.insertMeal(meal1)
        diaryRepository.insertMeal(meal2)
        viewModel.deleteMeal(meal1.mealId)
        val meals = viewModel.meals.first()
        val meal1WithFoodEntries = MealWithFoodEntries(
            meal1,
            listOf()
        )
        assertThat(meals).doesNotContain(meal1WithFoodEntries)
    }

    @Test
    fun `when createMeal() is called, should create meal`() = runTest {
        val meal = MealWithFoodEntries(
            Meal(
                title = "Omelette",
                instructions = "Put in pan"
            ),
            listOf()
        )
        viewModel.createMeal("Omelette", "Put in pan")
        val meals = diaryRepository.getAllMealsWithFoodEntries().first()
        assertThat(meals).contains(meal)
    }
}