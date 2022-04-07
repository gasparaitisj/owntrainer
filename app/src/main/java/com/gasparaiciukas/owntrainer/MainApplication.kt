package com.gasparaiciukas.owntrainer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.gasparaiciukas.owntrainer.prefs.PrefsStoreImpl
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
            // Get Dark Mode from Settings
            var isDarkMode = false
            prefsStore.isDarkMode()
                .take(1)
                .catch { e -> println(e.stackTrace) }
                .collect { isDarkMode = it }
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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