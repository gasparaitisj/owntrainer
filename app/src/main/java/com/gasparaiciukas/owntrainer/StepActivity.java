package com.gasparaiciukas.owntrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class StepActivity extends AppCompatActivity {

    // Initialize variables
    private static final String TAG = "SensorEvent";
    private boolean isServiceStopped;
    private int currentSteps;
    private float previousTotalSteps;
    private float totalSteps;
    private TextView tStepCounter;



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

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        totalSteps = intent.getFloatExtra("totalSteps", 0);
        currentSteps = (int) totalSteps - (int) previousTotalSteps;
        tStepCounter.setText(String.valueOf(currentSteps));
        }
    };

    // Resets step counter, when long clicked on step counter
    private void resetSteps() {
        tStepCounter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previousTotalSteps = totalSteps;
                currentSteps = (int) totalSteps - (int) previousTotalSteps;
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
        editor.putFloat("previousTotalSteps", previousTotalSteps);
        editor.putFloat("totalSteps", totalSteps);
        editor.putInt("currentSteps", currentSteps);
        editor.apply();
        Log.d("test", "Saved: Previous total steps: " + String.valueOf(previousTotalSteps));
        Log.d("test", "Saved: Total steps: " + String.valueOf(totalSteps));
        Log.d("test", "Saved: Current steps: " + String.valueOf(totalSteps - previousTotalSteps));
    }

    // Load data (steps taken)
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        previousTotalSteps = sharedPreferences.getFloat("previousTotalSteps", 0f);
        totalSteps = sharedPreferences.getFloat("totalSteps", 0f);
        currentSteps = sharedPreferences.getInt("currentSteps", 0);
        Log.d("test", "Loaded: Previous total steps: " + String.valueOf(previousTotalSteps));
        Log.d("test", "Loaded: Total steps: " + String.valueOf(totalSteps));
        Log.d("test", "Loaded: Current steps: " + String.valueOf(currentSteps));
    }

    public void onStopButtonClicked(View view) {
        stopService(new Intent(getBaseContext(), StepCounterService.class));
        saveData();
    }

    public void onStartButtonClicked(View view) {
        // Start intent
        Log.d("test", "Data before starting service:");
        Log.d("test", "Previous total steps: " + String.valueOf(previousTotalSteps));
        Log.d("test", "Total steps: " + String.valueOf(totalSteps));
        Log.d("test", "Current steps: " + String.valueOf(currentSteps));

        Intent intent = new Intent(this, StepCounterService.class);
        intent.putExtra("totalSteps", totalSteps);
        intent.putExtra("previousTotalSteps", previousTotalSteps);

        // Start service
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_ACTION));
        isServiceStopped = false;
    }
}