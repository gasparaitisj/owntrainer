package com.gasparaiciukas.owntrainer.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gasparaiciukas.owntrainer.fragment.IntroDetailsFragment;
import com.gasparaiciukas.owntrainer.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

import org.jetbrains.annotations.Nullable;

public class IntroActivity extends AppIntro {

    private IntroDetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detailsFragment = IntroDetailsFragment.newInstance();
        // Add slides
        addSlide(AppIntroFragment.newInstance(
                "Welcome to owntrainer!",
                "Welcome to the all-in-one fitness app - " +
                        "let's get started!",
                R.drawable.intro_fitnesstracker,
                ContextCompat.getColor(this, R.color.colorGold)));

        addSlide(AppIntroFragment.newInstance(
                "Welcome to owntrainer!",
                "In order for the pedometer to work,\n" +
                        "physical activity tracking MUST be permitted",
                R.drawable.intro_walking,
                ContextCompat.getColor(this, R.color.colorGold)));

        addSlide(detailsFragment);

        addSlide(AppIntroFragment.newInstance(
                "Welcome to owntrainer!",
                "In order for the app to work correctly,\n" +
                        "it is recommended to turn off battery saver",
                R.drawable.intro_battery,
                ContextCompat.getColor(this, R.color.colorGold)));

        addSlide(AppIntroFragment.newInstance(
                "Welcome to owntrainer!",
                "In order to work correctly,\n" +
                        "it is recommended to turn off battery saver",
                R.drawable.intro_battery,
                ContextCompat.getColor(this, R.color.colorGold)));

        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        setWizardMode(true);
        setSystemBackButtonLocked(true);

        // Ask for permissions on the 2nd slide
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            askForPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.FOREGROUND_SERVICE}, 2, false);
        }
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // If app intro is completed, don't show it again
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putBoolean("firstStart", false);
        e.apply();
    }
}