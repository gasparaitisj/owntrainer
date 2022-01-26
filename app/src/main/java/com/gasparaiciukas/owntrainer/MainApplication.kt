package com.gasparaiciukas.owntrainer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Get Dark Mode from Settings
        if (isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Timber Logging
        Timber.plant(CustomTimberDebugTree("judicialGranite"))
    }

    private fun isDarkMode(): Boolean {
        return getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("darkMode", false)
    }
}

class CustomTimberDebugTree(val tagPrefix: String) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "$tagPrefix.$tag", message, t)  }
}