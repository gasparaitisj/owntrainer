package com.gasparaiciukas.owntrainer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.gasparaiciukas.owntrainer.prefs.PrefsStoreImpl
import com.gasparaiciukas.owntrainer.viewmodel.AppearanceMode
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var prefsStore: PrefsStoreImpl

    override fun onCreate() {
        super.onCreate()
        val scope = (CoroutineScope(SupervisorJob()))
        scope.launch {
            // Get Appearance Mode from Settings
            var appearanceMode = AppearanceMode.SYSTEM_DEFAULT
            prefsStore.getAppearanceMode()
                .take(1)
                .catch { e -> println(e.stackTrace) }
                .collect { appearanceMode = AppearanceMode.values()[it] }
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

            // Timber Logging
            Timber.plant(CustomTimberDebugTree("judicialGranite"))
        }
    }
}

class CustomTimberDebugTree(private val tagPrefix: String) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "$tagPrefix.$tag", message, t)
    }
}