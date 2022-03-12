package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateMealItemViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CreateMealItemViewModel
    private lateinit var diaryRepository: FakeDiaryRepository

    @Before
    fun setup() {
        diaryRepository = FakeDiaryRepository()
        viewModel = CreateMealItemViewModel(diaryRepository)
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
        val meals = diaryRepository.getMealsWithFoodEntries().asLiveData().getOrAwaitValueTest()
        assertThat(meals).contains(meal)
    }
}