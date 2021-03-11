package com.gasparaiciukas.owntrainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StepActivity extends AppCompatActivity {

    private int currentSteps;
    private TextView tStepCounter;
    private int countedSteps;
    private int previousCountedSteps;
    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        // Step counter
        tStepCounter = findViewById(R.id.stepCounterBackground);

        // Load previous steps taken
        loadData();

        // Allow to reset steps
        resetSteps();

        // Put previous service step count on screen
        tStepCounter.setText(String.valueOf(currentSteps));
    }

    // Set up broadcast receiver to update current step count
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            countedSteps = intent.getIntExtra("countedSteps", 0);
            //Log.d("test", "Received " + countedSteps + " steps.");

            currentSteps += (countedSteps - previousCountedSteps);
            previousCountedSteps = countedSteps;
            tStepCounter.setText(String.valueOf(currentSteps));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    // Resets step counter, when long clicked on step counter
    private void resetSteps() {
        tStepCounter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currentSteps = 0;
                tStepCounter.setText(String.valueOf(currentSteps));
                saveData();
                Log.d("test", "Steps reset!");
                return true;
            }
        });
    }

    // Save data (steps taken)
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentSteps", currentSteps);
        editor.apply();
        Log.d("test", "Saved: Current steps: " + String.valueOf(currentSteps));
    }

    // Load data (steps taken)
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        currentSteps = sharedPreferences.getInt("currentSteps", 0);
        Log.d("test", "Loaded: Current steps: " + String.valueOf(currentSteps));
    }

    public void onStopButtonClicked(View view) {
        if (isServiceRunning) {
            stopService(new Intent(this, StepCounterService.class));
            unregisterReceiver(broadcastReceiver);
            saveData();
            isServiceRunning = false;
        }
    }

    public void onStartButtonClicked(View view) {
        if (!isServiceRunning) {
            // Ask for physical activity permission if not provided
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
                }
            }

            // Set up service intent
            Intent intent = new Intent(this, StepCounterService.class);
            intent.putExtra("currentSteps", currentSteps);

            // Start service
            startService(intent);
            isServiceRunning = true;
            registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_ACTION));
        }
    }
}