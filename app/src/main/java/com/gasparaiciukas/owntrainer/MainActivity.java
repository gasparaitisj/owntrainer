package com.gasparaiciukas.owntrainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            transaction.replace(R.id.fragment_frame_layout, selectedFragment);
            transaction.commit();
            return true;
        });

        // Show home fragment on startup
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_frame_layout, MainFragment.newInstance());
        transaction.commit();
    }
}