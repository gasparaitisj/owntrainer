package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.DiaryEntryMealCrossRef
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddMealToDiaryViewModelTest {
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
    fun `when addMealToDiary() is called, should add meal to diary`() = runTest {
        val foodEntry = FoodEntry(
            mealId = 1,
            title = "Apple",
            caloriesPer100G = 52.0,
            carbsPer100G = 14.3,
            fatPer100G = 0.65,
            proteinPer100G = 0.0,
            quantityInG = 60.0
        )
        val meal = Meal(
            mealId = 1,
            title = "Apple pie",
            instructions = "Put in oven"
        )
        val diaryEntry = DiaryEntry(
            diaryEntryId = 1,
            year = 2021,
            dayOfYear = 1,
            dayOfWeek = 1,
            monthOfYear = 1,
            dayOfMonth = 1
        )
        diaryRepository.insertMeal(meal)
        diaryRepository.insertFood(foodEntry)
        diaryRepository.insertDiaryEntry(diaryEntry)
        diaryRepository.insertDiaryEntryMealCrossRef(DiaryEntryMealCrossRef(1, 1))

        viewModel.user = userRepository.user.asLiveData().getOrAwaitValueTest()
        viewModel.diaryEntryWithMeals = diaryRepository.getDiaryEntryWithMeals(
            2021,
            1
        ).asLiveData().getOrAwaitValueTest()
        viewModel.calculateData()

        viewModel.addMealToDiary(diaryRepository.getMealWithFoodEntriesById(1))
        val diaryEntryWithMeals = diaryRepository.getDiaryEntryWithMeals(2021, 1).asLiveData().getOrAwaitValueTest()
        assertThat(diaryEntryWithMeals.meals).contains(meal)
    }
}