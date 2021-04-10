package com.gasparaiciukas.owntrainer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gasparaiciukas.owntrainer.fragment.FoodFragment;
import com.gasparaiciukas.owntrainer.fragment.DiaryFragment;
import com.gasparaiciukas.owntrainer.fragment.ProgressFragment;
import com.gasparaiciukas.owntrainer.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatabase();
        initAppIntroThread();
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi() {
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
        BottomNavigationView bottomNavigation = findViewById(R.id.main_bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;
            String selectedFragmentTag;
            switch (item.getItemId()) {
                // Home fragment
                case R.id.navbar_item_1:
                    selectedFragment = DiaryFragment.newInstance();
                    selectedFragmentTag = "DIARY_FRAGMENT";
                    break;
                // Pedometer activity
                case R.id.navbar_item_2:
                    this.startActivity(new Intent(this, StepActivity.class));
                    return true;
                // Food fragment
                case R.id.navbar_item_3:
                    selectedFragment = FoodFragment.newInstance();
                    selectedFragmentTag = "FOOD_FRAGMENT";
                    break;
                // Progress fragment
                case R.id.navbar_item_4:
                    selectedFragment = ProgressFragment.newInstance();
                    selectedFragmentTag = "PROGRESS_FRAGMENT";
                    break;
                default:
                    return false;
            }

            // Show selected fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_frame_layout, selectedFragment, selectedFragmentTag);
            transaction.commit();
            return true;
        });
    }

    private void initAppIntroThread() {
        // Start app intro, if app launched for the first time
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {
                    // Diary
                    SharedPreferences sharedPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("year", LocalDate.now().getYear());
                    editor.putInt("month", LocalDate.now().getMonthValue());
                    editor.putInt("day", LocalDate.now().getDayOfMonth());
                    editor.apply();

                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });
                }
                else {
                    // Show home fragment
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment_frame_layout, DiaryFragment.newInstance(), "DIARY_FRAGMENT");
                    transaction.commit();
                }
            }
        });
        t.start();
    }

    private void initDatabase() {
        // Initialize Realm database
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().
                allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
