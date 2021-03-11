package com.gasparaiciukas.owntrainer;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class StepCounterService extends Service implements SensorEventListener {

    // Initialize variables
    private SensorManager sensorManager = null;
    private float totalSteps;
    private float previousTotalSteps;
    private int currentSteps;
    private int countedSteps;
    private boolean firstTrigger = true;
    private Intent broadcastIntent;
    public static final String BROADCAST_ACTION = "secretCode123";
    private final Handler handler = new Handler();
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;


    @Override
    public void onCreate() {
        broadcastIntent = new Intent(BROADCAST_ACTION);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Load data
        currentSteps = intent.getIntExtra("currentSteps", 0);

        // Show notification
        showNotification();

        // Set up sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            Toast.makeText(this, "No step sensor detected!", Toast.LENGTH_SHORT).show();
        }
        else {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Set up handler
        handler.removeCallbacks(updateBroadcastData);
        handler.post(updateBroadcastData);

        return START_STICKY;
    }

    // Runnable for updating broadcast data
    private final Runnable updateBroadcastData = new Runnable() {
        public void run() {
                // Update total steps
                broadcastIntent.putExtra("countedSteps", countedSteps);
                sendBroadcast(broadcastIntent);

                // Call "handler.postDelayed" again, after a specified delay.
                //handler.post(this);
                Log.d("test", "Counted " + countedSteps + " steps.");
        }
    };

    // Stop service
    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissNotification();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER));
        handler.removeCallbacks(updateBroadcastData);
        Log.d("test", "Service stopped.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // do nothing
        return null;
    }

    // Update counted steps on sensor change
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (firstTrigger) {
            previousTotalSteps = event.values[0];
            firstTrigger = false;
        }
        totalSteps = event.values[0];
        countedSteps = (int) totalSteps - (int) previousTotalSteps;
        currentSteps += countedSteps;
        Log.d("test", "Sensor triggered!");
        updateNotification();
        handler.post(updateBroadcastData);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    // Create and show notification
    private void showNotification() {
        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("serviceNotification",
                    "serviceNotification", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Foreground service notification");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Build notification
        builder = new NotificationCompat.Builder(this, "myNotification")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Your current step count:")
                .setContentText(String.valueOf(currentSteps))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);
        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
        Log.d("test", "Notification shown!");
        // TODO: go to app, when notification pressed
    }

    // Update notification step count
    private void updateNotification() {
        builder.setContentText(String.valueOf(currentSteps));
        notificationManager.notify(1, builder.build());
        Log.d("test", "Notification updated!");
    }

    // Dismiss notification
    private void dismissNotification() {
        notificationManager.cancel(1);
        Log.d("test", "Notification dismissed!");
    }
}
