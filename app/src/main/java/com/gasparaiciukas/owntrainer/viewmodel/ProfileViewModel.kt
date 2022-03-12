package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.DefaultUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject internal constructor(
    private val userRepository: DefaultUserRepository
) : ViewModel() {
    val ldUser: LiveData<User> = userRepository.user.asLiveData()
    lateinit var user: User

    suspend fun writeUserToDatabase() {
        userRepository.updateUser(user)
    }
}