package com.gasparaiciukas.owntrainer.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.Reminder
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.UserRepository
import com.gasparaiciukas.owntrainer.utils.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val appearanceMode: Int,
    val user: User
)

@HiltViewModel
class SettingsViewModel @Inject internal constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val ldReminders = userRepository.getReminders().asLiveData()
    val ldAppearanceMode = userRepository.getAppearanceMode().asLiveData()
    val ldUser = switchMap(ldAppearanceMode) {
        userRepository.user.asLiveData()
    }
    val uiState = MutableLiveData<SettingsUiState>()

    var hour = 0
    var minute = 0
    var title = ""
    var isTitleCorrect = false
    var isTimeCorrect = false

    fun loadUiState() {
        safeLet(ldAppearanceMode.value, ldUser.value) { appearanceMode, user ->
            uiState.postValue(
                SettingsUiState(
                    appearanceMode = appearanceMode,
                    user = user
                )
            )
        }
    }

    fun setAppearanceMode(appearanceMode: AppearanceMode) {
        viewModelScope.launch {
            when (appearanceMode) {
                AppearanceMode.SYSTEM_DEFAULT -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                AppearanceMode.DAY -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                AppearanceMode.NIGHT -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            userRepository.setAppearanceMode(appearanceMode)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }

    fun createReminder(title: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            userRepository.insertReminder(
                Reminder(
                    title = title,
                    hour = hour,
                    minute = minute
                )
            )
        }
    }

    fun deleteReminder(reminderId: Int) {
        viewModelScope.launch {
            userRepository.deleteReminderById(reminderId)
        }
    }
}

enum class AppearanceMode {
    SYSTEM_DEFAULT, DAY, NIGHT
}