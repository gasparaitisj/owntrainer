package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
        assertThat(viewModel.ldResponse.value!!.status).isEqualTo(Status.SUCCESS)

    }

    @Test
    fun `when getFoods() is called unsuccessfully, should return error`() = runTest {
        diaryRepository.setShouldReturnNetworkError(true)
        viewModel.getFoods("test")
        assertThat(viewModel.ldResponse.value!!.status).isEqualTo(Status.ERROR)
    }
}