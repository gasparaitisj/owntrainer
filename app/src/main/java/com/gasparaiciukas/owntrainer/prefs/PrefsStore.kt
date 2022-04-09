package com.gasparaiciukas.owntrainer.prefs

import com.gasparaiciukas.owntrainer.viewmodel.AppearanceMode
import kotlinx.coroutines.flow.Flow

interface PrefsStore {
    fun getAppearanceMode(): Flow<Int>
    suspend fun setAppearanceMode(appearanceMode: AppearanceMode)
}