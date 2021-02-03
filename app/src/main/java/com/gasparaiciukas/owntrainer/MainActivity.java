package com.gasparaiciukas.owntrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Initialize variables
    private SensorManager sensorManager = null;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private int currentSteps = 0;
    private TextView tStepCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step counter
        tStepCounter = findViewById(R.id.stepCounter);

        // Load previous steps taken
        loadData();

        // Allow to reset steps
        resetSteps();

        // Put current step count on screen
        currentSteps = (int) totalSteps - (int) previousTotalSteps;
        tStepCounter.setText(String.valueOf(currentSteps));

        // Set up the sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            Toast.makeText(this, "No step sensor detected!", Toast.LENGTH_SHORT).show();
        }
        else {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    // do nothing
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Update steps on sensor change
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            Log.d("test", "Sensor triggered!");
            totalSteps = event.values[0];
            currentSteps = (int) totalSteps - (int) previousTotalSteps;
            tStepCounter.setText(String.valueOf(currentSteps));
        }
    }

    // Resets step counter, when long clicked on step counter
    private void resetSteps() {
        tStepCounter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previousTotalSteps = totalSteps;
                tStepCounter.setText(String.valueOf(0));
                saveData();
                return true;
            }
        });
    }

    // Save data (steps taken)
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Log.d("test", "Previous total steps: " + String.valueOf(previousTotalSteps));
        //Log.d("test", "Total steps: " + String.valueOf(totalSteps));
        //Log.d("test", "Current steps: " + String.valueOf(totalSteps - previousTotalSteps));
        editor.putFloat("previousTotalSteps", previousTotalSteps);
        editor.putFloat("totalSteps", totalSteps);
        editor.apply();
    }

    // Load data (steps taken)
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        previousTotalSteps = sharedPreferences.getFloat("previousTotalSteps", 0f);
        totalSteps = sharedPreferences.getFloat("totalSteps", 0f);
    }
}