package com.gasparaiciukas.owntrainer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.MainCoroutineRule
import com.gasparaiciukas.owntrainer.getOrAwaitValueTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
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
        val user = userRepository.user.asLiveData().getOrAwaitValueTest().copy()
        user.weightInKg = 50.0
        viewModel.updateUser(user)
        val userTest = userRepository.user.asLiveData().getOrAwaitValueTest().copy()
        assertThat(user).isEqualTo(userTest)
    }
}