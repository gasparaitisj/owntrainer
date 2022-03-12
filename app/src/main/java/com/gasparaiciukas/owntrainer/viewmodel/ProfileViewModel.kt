package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject internal constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    lateinit var user: User

    suspend fun updateUser() = userRepository.updateUser(user)
}