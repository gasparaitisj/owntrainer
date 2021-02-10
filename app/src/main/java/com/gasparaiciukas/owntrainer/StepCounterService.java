package com.gasparaiciukas.owntrainer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class StepCounterService extends Service implements SensorEventListener {

    // Initialize variables
    private SensorManager sensorManager = null;

    private float totalSteps;
    private float previousTotalSteps;
    private int currentSteps;
    private Intent broadcastIntent;
    public static final String BROADCAST_ACTION = "secretCode123";
    private final Handler handler = new Handler();
    private boolean serviceStopped;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;


    @Override
    public void onCreate() {
        broadcastIntent = new Intent(BROADCAST_ACTION);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Load data
        totalSteps = intent.getFloatExtra("totalSteps", 0);
        previousTotalSteps = intent.getFloatExtra("previousTotalSteps", 0);
        currentSteps = (int) totalSteps - (int) previousTotalSteps;

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

        // Set up service handler
        serviceStopped = false;

        // remove any existing callbacks to the handler
        handler.removeCallbacks(updateBroadcastData);

        // call our handler with or without delay.
        handler.post(updateBroadcastData); // 0 seconds

        return START_STICKY;
    }

    // Runnable for updating broadcast data
    private final Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) {
                broadcastIntent.putExtra("totalSteps", totalSteps);
                sendBroadcast(broadcastIntent);

                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }
        }
    };

    // Stop service and dismiss notification on destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceStopped = true;
        dismissNotification();
        Log.d("test", "Service stopped.");
    }

    // Binding not implemented
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Update counted steps on sensor change
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("test", "Sensor triggered!");
        totalSteps = event.values[0];
        updateNotification();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    // Show notification
    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myNotification";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("myNotification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(this, "myNotification")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Your current step count:")
                .setContentText(String.valueOf(currentSteps))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
        //Log.d("test", "Notification shown!");
    }

    private void updateNotification() {
        currentSteps = (int) totalSteps - (int) previousTotalSteps;
        builder.setContentText(String.valueOf(currentSteps));
        notificationManager.notify(1, builder.build());
        // Log.d("test", "Notification updated!");
    }

    private void dismissNotification() {
        notificationManager.cancel(1);
        // Log.d("test", "Notification dismissed!");
    }
}
