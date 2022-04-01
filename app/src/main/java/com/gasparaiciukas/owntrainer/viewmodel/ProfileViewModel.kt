package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.*
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject internal constructor(
    val userRepository: UserRepository
) : ViewModel() {
    val ldUser = userRepository.user.asLiveData() as MutableLiveData<User>

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user)
            ldUser.postValue(user)
        }
    }
}