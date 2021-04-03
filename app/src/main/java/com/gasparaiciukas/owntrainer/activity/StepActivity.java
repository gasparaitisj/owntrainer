package com.gasparaiciukas.owntrainer.activity;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.database.PedometerEntry;
import com.gasparaiciukas.owntrainer.database.User;
import com.gasparaiciukas.owntrainer.service.StepCounterService;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.time.LocalDate;

import io.realm.Realm;

public class StepActivity extends AppCompatActivity {

    // Variables
    private int currentSteps;
    private int countedSteps;
    private int previousCountedSteps = 0;
    private int calories;
    private double distance;
    private boolean isStepCounterServiceRunning;
    private int previousDay;
    private int dailyStepGoal;
    private double kcalBurnedPerStep;
    private double stepLengthInCm;
    private int timeElapsedInS;
    private long startTimeInS;
    private long stopTimeInS;
    private int totalTimeElapsedInS;
    private final String TAG = "stepActivityTest";

    // UI
    private CircularProgressBar stepProgressBar;
    private TextView tStepProgress;
    private Button toggleStepCounterButton;
    private TextView tCalories;
    private TextView tDistance;
    private TextView tDailyStepGoal;
    private TextView tTimeElapsedH;
    private TextView tTimeElapsedM;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        stepProgressBar = findViewById(R.id.step_counter);
        tStepProgress = findViewById(R.id.step_current_count);
        toggleStepCounterButton = findViewById(R.id.toggle_step_counter_button);
        tCalories = findViewById(R.id.step_calorie_count);
        tDistance = findViewById(R.id.step_distance_count);
        tDailyStepGoal = findViewById(R.id.step_daily_goal_count);
        tTimeElapsedH = findViewById(R.id.step_time_count_h);
        tTimeElapsedM = findViewById(R.id.step_time_count_m);

        // Load data
        loadData();
        calories = (int) (currentSteps * kcalBurnedPerStep);
        distance = currentSteps * (stepLengthInCm / 100000);

        // If it is the next day, reset steps and save that day's steps to database
        //Log.d(TAG, "onCreate: " + previousDay + " " + LocalDate.now().getDayOfYear());
        if (previousDay != LocalDate.now().getDayOfYear())
            saveDay();

        // Set listener to reset steps
        resetStepsOnClick();

        isStepCounterServiceRunning = isServiceRunning();
        //Log.d("test", "Service is running:" + isServiceRunning);

        // Show data
        stepProgressBar.setProgress(currentSteps);
        tStepProgress.setText(String.valueOf(currentSteps));
        tCalories.setText(String.valueOf(calories));
        tDistance.setText(String.format("%.2f", distance));
        tDailyStepGoal.setText(String.valueOf(dailyStepGoal));
        tTimeElapsedH.setText(String.valueOf(totalTimeElapsedInS / 3600));
        tTimeElapsedM.setText(String.valueOf((totalTimeElapsedInS % 3600) / 60));

        if (isStepCounterServiceRunning)
            toggleStepCounterButton.setText(R.string.stop);
        else
            toggleStepCounterButton.setText(R.string.start);

        // Set up intent
        serviceIntent = new Intent(this, StepCounterService.class);
        serviceIntent.putExtra("currentSteps", currentSteps);


        toggleStepCounterButton.setOnClickListener(v -> {
            if (isStepCounterServiceRunning)
                onStopButtonClicked();
            else
                onStartButtonClicked();
        });
    }

    // Set up broadcast receiver to update current step count
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        // Receive counted steps
        countedSteps = intent.getIntExtra("countedSteps", 0);
        //Log.d("test", "Received " + countedSteps + " steps.");

        // Update counted steps
        currentSteps += (countedSteps - previousCountedSteps);
        previousCountedSteps = countedSteps;
        saveSteps();
        calories = (int) (currentSteps * kcalBurnedPerStep);
        distance = currentSteps * (stepLengthInCm / 100000);

        stepProgressBar.setProgressWithAnimation(currentSteps);
        tStepProgress.setText(String.valueOf(currentSteps));
        tCalories.setText(String.valueOf(calories));
        tDistance.setText(String.format("%.2f", distance));
        //Log.d("test", "Bar updated! (Steps: " + currentSteps + ")");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (isServiceRunning) destroyService();
        //Log.d("test", "Activity destroyed!");
    }

    private void resetSteps() {
        // Reset data
        currentSteps = 0;
        calories = 0;
        distance = 0;
        totalTimeElapsedInS = 0;

        // Reset UI
        stepProgressBar.setProgressWithAnimation(currentSteps);
        tStepProgress.setText(String.valueOf(currentSteps));
        tCalories.setText(String.valueOf(calories));
        tDistance.setText(String.format("%.2f", distance));
        tTimeElapsedH.setText(String.valueOf(totalTimeElapsedInS / 3600));
        tTimeElapsedM.setText(String.valueOf((totalTimeElapsedInS % 3600) / 60));
        saveSteps();
        //Log.d("test", "Steps reset!");
    }

    // Resets step counter, when long clicked on step counter
    private void resetStepsOnClick() {
        stepProgressBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resetSteps();
                return true;
            }
        });
    }

    private void saveSteps() {
        // Save steps to shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentSteps", currentSteps);
        editor.putInt("totalTimeElapsedInS", totalTimeElapsedInS);
        editor.apply();
        //Log.d("test", "Saved: Current steps: " + String.valueOf(currentSteps));
    }

    private void saveDay() {
        // Save day to shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        boolean isFirstStart = sharedPreferences.getBoolean("isFirstStart", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        previousDay = LocalDate.now().getDayOfYear();
        editor.putInt("previousDay", previousDay);
        editor.apply();

        // Save steps of previous day to database
        Realm realm = Realm.getDefaultInstance();
        PedometerEntry e = new PedometerEntry();
        e.setCalories(calories);
        e.setDayOfYear(previousDay);
        e.setDistanceInKm(distance);
        e.setSteps(currentSteps);
        e.setTimeElapsedInS(totalTimeElapsedInS);
        e.setYear(LocalDate.now().getYear());
        e.setYearAndDayOfYear(String.valueOf(LocalDate.now().getYear()) + String.valueOf(previousDay));
        realm.executeTransaction(r -> r.insertOrUpdate(e));
//        RealmResults<PedometerEntry> entries = realm.where(PedometerEntry.class).findAll();
//        for (PedometerEntry entry : entries) {
//            Log.d(TAG, entry.getYear() + " " + entry.getDayOfYear() + " " +
//                    entry.getSteps() + " " + entry.getCalories() + " " +
//                    entry.getDistanceInKm() + " " + entry.getTimeElapsedInS());
//        }
        realm.close();

        // Handle first start
        if (isFirstStart) {
            editor.putBoolean("isFirstStart", false);
            editor.apply();
        }
        else {
            resetSteps();
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        currentSteps = sharedPreferences.getInt("currentSteps", 0);
        previousDay = sharedPreferences.getInt("previousDay", 0);
        totalTimeElapsedInS = sharedPreferences.getInt("totalTimeElapsedInS", 0);
        Realm realm = Realm.getDefaultInstance();
        User u = realm.where(User.class)
                .equalTo("userId", "user")
                .findFirst();
        dailyStepGoal = u.getDailyStepGoal();
        kcalBurnedPerStep = u.getKcalBurnedPerStep();
        stepLengthInCm = u.getStepLengthInCm();
        realm.close();
        //Log.d("test", "Loaded: Current steps: " + String.valueOf(currentSteps));
    }

    private void onStopButtonClicked() {
        // Destroy service and reload activity
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
        registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_PEDOMETER));
        toggleStepCounterButton.setText(R.string.stop);
        startTimeInS = (System.currentTimeMillis() / 1000);
    }

    // Stop service and receiver
    private void destroyService() {
        try {
            // try to unregister receiver
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ignored) {
            // ignore exception if receiver already unregistered
        }
        stopTimeInS = (System.currentTimeMillis() / 1000);
        timeElapsedInS = (int) (stopTimeInS - startTimeInS);
        totalTimeElapsedInS += timeElapsedInS;
        stopService(serviceIntent);
        saveSteps();
        isStepCounterServiceRunning = false;
        toggleStepCounterButton.setText(R.string.start);
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StepCounterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}