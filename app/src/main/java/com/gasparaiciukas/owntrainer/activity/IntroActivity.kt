package com.gasparaiciukas.owntrainer.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.fragment.IntroDetailsFragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType

class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlides()
        getPermissions()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }

    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            askForPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.FOREGROUND_SERVICE), 2, false)
        }
    }

    private fun addSlides() {
        val detailsFragment = IntroDetailsFragment.newInstance()
        addSlide(AppIntroFragment.newInstance(
                title = "Welcome to owntrainer!",
                description = "Welcome to the all-in-one fitness app - let's get started!",
                imageDrawable = R.drawable.intro_fitnesstracker,
                backgroundColor = ContextCompat.getColor(this, R.color.colorGold)))
        addSlide(AppIntroFragment.newInstance(
                title = "Welcome to owntrainer!",
                description = "In order for the pedometer to work,\n" +
                        "physical activity tracking MUST be permitted",
                imageDrawable = R.drawable.intro_walking,
                backgroundColor = ContextCompat.getColor(this, R.color.colorGold)))
        addSlide(detailsFragment)
        addSlide(AppIntroFragment.newInstance(
                title = "Welcome to owntrainer!",
                " In order for the app to work correctly,\n " +
                        "it is recommended to turn off battery saver",
                R.drawable.intro_battery,
                ContextCompat.getColor(this, R.color.colorGold)))

        // Add fade transition
        setTransformer(AppIntroPageTransformerType.Fade)

        // Enable back button instead of skip
        isWizardMode = true

        // Lock system back button
        isSystemBackButtonLocked = true
    }
}