package com.gasparaiciukas.owntrainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class StepActivity extends AppCompatActivity {

    // Variables
    private int currentSteps;
    private int countedSteps;
    private int previousCountedSteps = 0;
    private int calories;
    private double distance;
    private boolean isStepCounterServiceRunning;

    // UI
    private CircularProgressBar stepProgressBar;
    private TextView stepProgressText;
    private Button toggleStepCounterButton;
    private TextView caloriesText;
    private TextView distanceText;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        stepProgressBar = findViewById(R.id.stepCounter);
        stepProgressText = findViewById(R.id.stepCurrentCount);
        toggleStepCounterButton = findViewById(R.id.toggleStepCounterButton);
        caloriesText = findViewById(R.id.stepCalorieCount);
        distanceText = findViewById(R.id.stepDistanceCount);

        // Load previous steps taken
        loadData();

        // Allow to reset steps
        resetSteps();

        isStepCounterServiceRunning = isServiceRunning(StepCounterService.class);
        //Log.d("test", "Service is running:" + isServiceRunning);

        // Show data
        calories = (int) (currentSteps * 0.05); // 0.05 is a temp value
        distance = currentSteps * 0.00075; // 0.00075 is a temp value
        stepProgressBar.setProgress(currentSteps);
        stepProgressText.setText(String.valueOf(currentSteps));
        caloriesText.setText(String.valueOf(calories));
        distanceText.setText(String.format("%.2f", distance));

        if (isStepCounterServiceRunning)
            toggleStepCounterButton.setText("Stop");
        else
            toggleStepCounterButton.setText("Start");

        // Set up intent
        serviceIntent = new Intent(this, StepCounterService.class);
        serviceIntent.putExtra("currentSteps", currentSteps);


        toggleStepCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStepCounterServiceRunning)
                    onStopButtonClicked();
                else
                    onStartButtonClicked();
            }
        });
    }

    // Set up broadcast receiver to update current step count
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        // Receive counted steps
        countedSteps = intent.getIntExtra("countedSteps", 0);
        Log.d("test", "Received " + countedSteps + " steps.");

        // Update counted steps
        currentSteps += (countedSteps - previousCountedSteps);
        previousCountedSteps = countedSteps;
        saveData();
        calories = (int) (currentSteps * 0.05); // 0.05 is a temp value
        distance = currentSteps * 0.00075; // 0.00075 is a temp value

        stepProgressBar.setProgressWithAnimation(currentSteps);
        stepProgressText.setText(String.valueOf(currentSteps));
        caloriesText.setText(String.valueOf(calories));
        distanceText.setText(String.format("%.2f", distance));
        Log.d("test", "Bar updated! (Steps: " + currentSteps + ")");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (isServiceRunning) destroyService();
        //Log.d("test", "Activity destroyed!");
    }

    // Resets step counter, when long clicked on step counter
    private void resetSteps() {
        stepProgressBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currentSteps = 0;
                calories = 0;
                distance = 0;
                stepProgressBar.setProgressWithAnimation(currentSteps);
                stepProgressText.setText(String.valueOf(currentSteps));
                caloriesText.setText(String.valueOf(calories));
                distanceText.setText(String.format("%.2f", distance));
                saveData();
                //Log.d("test", "Steps reset!");
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
        //Log.d("test", "Saved: Current steps: " + String.valueOf(currentSteps));
    }

    // Load data (steps taken)
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        currentSteps = sharedPreferences.getInt("currentSteps", 0);
        //Log.d("test", "Loaded: Current steps: " + String.valueOf(currentSteps));
    }

    // Destroy service and reload activity
    private void onStopButtonClicked() {
        destroyService();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void onStartButtonClicked() {
        // Ask for physical activity permission if not provided
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }
        }

        // Start service
        startService(serviceIntent);
        isStepCounterServiceRunning = true;
        registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_ACTION));
        toggleStepCounterButton.setText("Stop");
    }

    // Stop service and receiver
    private void destroyService() {
        try {unregisterReceiver(broadcastReceiver);} // try to unregister receiver
        catch (IllegalArgumentException ignored){} // ignore exception if receiver already unregistered
        stopService(serviceIntent);
        saveData();
        isStepCounterServiceRunning = false;
        toggleStepCounterButton.setText("Start");
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}