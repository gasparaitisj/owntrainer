package com.gasparaiciukas.owntrainer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gasparaiciukas.owntrainer.fragment.FoodFragment;
import com.gasparaiciukas.owntrainer.fragment.MainFragment;
import com.gasparaiciukas.owntrainer.fragment.ProgressFragment;
import com.gasparaiciukas.owntrainer.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Database
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().
                allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        // Start app intro, if app launched for the first time
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });
                }
            }
        });

        // Start the thread
        t.start();

        setContentView(R.layout.activity_main);

        // Settings (top app bar)
        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.top_app_bar_settings) {
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                return true;
            }
            return false;
        });


        // Bottom navigation bar
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                // Home fragment
                case R.id.navbar_item_1:
                    selectedFragment = MainFragment.newInstance();
                    break;
                // Pedometer activity
                case R.id.navbar_item_2:
                    this.startActivity(new Intent(this, StepActivity.class));
                    return true;
                // Food fragment
                case R.id.navbar_item_3:
                    selectedFragment = FoodFragment.newInstance();
                    break;
                // Progress fragment
                case R.id.navbar_item_4:
                    selectedFragment = ProgressFragment.newInstance();
                    break;
                default:
                    return false;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_frame_layout, selectedFragment);
            transaction.commit();
            return true;
        });

        // Show home fragment on startup
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_frame_layout, MainFragment.newInstance());
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
