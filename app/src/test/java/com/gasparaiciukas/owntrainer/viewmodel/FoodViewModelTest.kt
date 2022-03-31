package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setup() {
        diaryRepository = FakeDiaryRepository()
        viewModel = FoodViewModel(diaryRepository)
    }

    @Test
    fun `when getFoods() is called successfully, should get foods`() = runTest {
        diaryRepository.setShouldReturnNetworkError(false)
        viewModel.getFoods("test")
        val response = viewModel.ldResponse.getOrAwaitValueTest()
        assertThat(response.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `when getFoods() is called unsuccessfully, should return error`() = runTest {
        diaryRepository.setShouldReturnNetworkError(true)
        viewModel.getFoods("test")
        val response = viewModel.ldResponse.getOrAwaitValueTest()
        assertThat(response.status).isEqualTo(Status.ERROR)
    }
}