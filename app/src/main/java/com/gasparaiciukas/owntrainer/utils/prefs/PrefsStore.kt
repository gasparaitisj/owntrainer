package com.gasparaiciukas.owntrainer.utils.prefs

import com.gasparaiciukas.owntrainer.utils.viewmodel.AppearanceMode
import kotlinx.coroutines.flow.Flow

interface PrefsStore {
    fun getAppearanceMode(): Flow<Int>
    suspend fun setAppearanceMode(appearanceMode: AppearanceMode)
}
