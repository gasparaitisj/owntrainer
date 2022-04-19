package com.gasparaiciukas.owntrainer.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.Reminder
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val appearanceMode: Int,
    val user: User,
    val reminders: List<Reminder>
)

@HiltViewModel
class SettingsViewModel @Inject internal constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val reminders = userRepository.getReminders()
    val appearanceMode = userRepository.getAppearanceMode()
    val user = userRepository.user
    val uiState = combine(reminders, appearanceMode, user) { lReminders, lAppearanceMode, lUser ->
        SettingsUiState(
            appearanceMode = lAppearanceMode,
            user = lUser,
            reminders = lReminders
        )
    }

    var hour = 0
    var minute = 0
    var title = ""
    var isTitleCorrect = false
    var isTimeCorrect = false

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