package com.gasparaiciukas.owntrainer.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.gasparaiciukas.owntrainer.prefs.PreferencesKeys.SETTINGS_PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PrefsStoreImpl @Inject constructor(
    @ApplicationContext val context: Context
) : PrefsStore {
    private val Context.dataStore by preferencesDataStore(SETTINGS_PREFERENCE_NAME)

    override fun isDarkMode() = context.dataStore.data.catch { exception ->
        // dataStore.data throws an IOException if it can't read the data
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { it[PreferencesKeys.IS_DARK_MODE] ?: false }

    override suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.IS_DARK_MODE] = isDarkMode
        }
    }
}

private object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("IS_DARK_MODE")
    const val SETTINGS_PREFERENCE_NAME = "settings"
}