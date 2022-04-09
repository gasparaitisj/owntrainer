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
    val appearanceMode: Int
)

@HiltViewModel
class SettingsViewModel @Inject internal constructor(
    private val prefsStore: PrefsStoreImpl
) : ViewModel() {
    val ldAppearanceMode = prefsStore.getAppearanceMode().asLiveData()

    val uiState = MutableLiveData<SettingsUiState>()

    fun loadUiState() {
        ldAppearanceMode.value?.let { appearanceMode ->
            uiState.postValue(
                SettingsUiState(
                    appearanceMode = appearanceMode
                )
            )
        }
    }

    fun setAppearanceMode(appearanceMode: AppearanceMode) {
        viewModelScope.launch {
            when(appearanceMode) {
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
            prefsStore.setAppearanceMode(appearanceMode)
        }
    }
}

enum class AppearanceMode {
    SYSTEM_DEFAULT, DAY, NIGHT
}