package com.gasparaiciukas.owntrainer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set dark mode to follow system
        if (isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Timber Logging
        Timber.plant(Timber.DebugTree())
    }

    private fun isDarkMode() : Boolean {
        return getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("darkMode", false)
    }

}