package com.gasparaiciukas.owntrainer.utils.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gasparaiciukas.owntrainer.settings.AppearanceMode
import com.gasparaiciukas.owntrainer.utils.prefs.PreferencesKeys.SETTINGS_PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PrefsStoreImpl @Inject constructor(
    @ApplicationContext val context: Context
) : PrefsStore {
    private val Context.dataStore by preferencesDataStore(SETTINGS_PREFERENCE_NAME)

    override fun getAppearanceMode() = context.dataStore.data.catch { exception ->
        // dataStore.data throws an IOException if it can't read the data
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { it[PreferencesKeys.APPEARANCE_MODE] ?: AppearanceMode.SYSTEM_DEFAULT.ordinal }

    override suspend fun setAppearanceMode(appearanceMode: AppearanceMode) {
        context.dataStore.edit {
            it[PreferencesKeys.APPEARANCE_MODE] = appearanceMode.ordinal
        }
    }
}

private object PreferencesKeys {
    val APPEARANCE_MODE = intPreferencesKey("APPEARANCE_MODE")
    const val SETTINGS_PREFERENCE_NAME = "settings"
}
