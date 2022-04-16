package com.gasparaiciukas.owntrainer.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var userRepository: FakeUserRepository

    @Before
    fun setup() {
        userRepository = FakeUserRepository()
        viewModel = SettingsViewModel(userRepository)
    }

    @Test
    fun `when loadUiState() is called, should load UI state`() = runTest {
        val user = userRepository.user.asLiveData().getOrAwaitValueTest().copy()
        user.ageInYears = 19
        val uiState = SettingsUiState(
            appearanceMode = AppearanceMode.NIGHT.ordinal,
            user = user
        )

        userRepository.setAppearanceMode(AppearanceMode.values()[uiState.appearanceMode])
        userRepository.updateUser(user)
        viewModel.ldAppearanceMode.getOrAwaitValueTest()
        viewModel.ldUser.getOrAwaitValueTest()
        viewModel.loadUiState()

        val uiStateTest = viewModel.uiState.getOrAwaitValueTest().copy()
        Truth.assertThat(uiStateTest).isEqualTo(uiState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    @Test
    fun `when setAppearanceMode() is called, should set appearance mode`() = runTest {
        viewModel.setAppearanceMode(AppearanceMode.DAY)
        Truth.assertThat(viewModel.ldAppearanceMode.getOrAwaitValueTest())
            .isEqualTo(AppearanceMode.DAY.ordinal)
        Truth.assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_NO)
    }
}