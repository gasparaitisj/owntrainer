package com.gasparaiciukas.owntrainer.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.prefs.PrefsStoreImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkMode: Boolean
)

@HiltViewModel
class SettingsViewModel @Inject internal constructor(
    private val prefsStore: PrefsStoreImpl
) : ViewModel() {
    val ldIsDarkMode = prefsStore.isDarkMode().asLiveData()

    val uiState = MutableLiveData<SettingsUiState>()

    fun loadUiState() {
        ldIsDarkMode.value?.let {
            uiState.postValue(
                SettingsUiState(
                    isDarkMode = it
                )
            )
        }
    }

    fun setDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                prefsStore.setDarkMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                prefsStore.setDarkMode(false)
            }
        }
    }
}