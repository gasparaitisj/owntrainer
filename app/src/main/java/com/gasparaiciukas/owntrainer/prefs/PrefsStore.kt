package com.gasparaiciukas.owntrainer.prefs

import kotlinx.coroutines.flow.Flow

interface PrefsStore {
    fun isDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(isDarkMode: Boolean)
}