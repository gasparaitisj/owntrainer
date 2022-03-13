package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var userRepository: FakeUserRepository

    @Before
    fun setup() {
        userRepository = FakeUserRepository()
        viewModel = ProfileViewModel(userRepository)
    }

    @Test
    fun `when updateUser() is called, should update user`() = runTest {
        viewModel.user = userRepository.user.asLiveData().getOrAwaitValueTest()
        val oldUser = viewModel.user.copy()
        viewModel.user.weightInKg = 70.0
        viewModel.updateUser()
        val updatedUser = userRepository.user.asLiveData().getOrAwaitValueTest()
        assertThat(updatedUser).isNotEqualTo(oldUser)
    }
}