package com.gasparaiciukas.owntrainer.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.activity.StepActivity;

public class StepCounterService extends Service implements SensorEventListener {

    // Variables
    private float totalSteps;
    private float previousTotalSteps = 0;
    private int currentSteps;
    private int countedSteps;
    private int previousCountedSteps = 0;

    // Sensor
    private SensorManager sensorManager = null;
    private Sensor stepSensor;

    // Broadcasting
    private Intent broadcastIntent;
    public static final String BROADCAST_PEDOMETER = "Pedometer notification";
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Notification
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private final int NOTIFICATION_ID_PEDOMETER = 1;

    @Override
    public void onCreate() {
        broadcastIntent = new Intent(BROADCAST_PEDOMETER);
        //Log.d("test", "Service created!");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Load data
        currentSteps = intent.getIntExtra("currentSteps", 0);

        // Create and show notification
        createNotification();

        // Set up sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            Toast.makeText(this, "No step sensor detected!", Toast.LENGTH_SHORT).show();
        }
        else {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }

        // Set up handler
        handler.removeCallbacks(updateBroadcastData);
        //handler.post(updateBroadcastData);

        return START_STICKY;
    }

    // Runnable for updating broadcast data
    private final Runnable updateBroadcastData = new Runnable() {
        public void run() {
                // Send counted steps
                broadcastIntent.putExtra("countedSteps", countedSteps);
                sendBroadcast(broadcastIntent);

                //Log.d("test", "Counted " + countedSteps + " steps.");
        }
    };

    // Stop service
    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissNotification();
        sensorManager.unregisterListener(this, stepSensor);
        handler.removeCallbacks(updateBroadcastData);
        //Log.d("test", "Service destroyed.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // do nothing
        return null;
    }

    // Broadcast counted steps and update notification
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (previousTotalSteps == 0) {
            previousTotalSteps = event.values[0];
        }
        else {
            totalSteps = event.values[0];
            //Log.d("test", "Previous total steps: " + String.valueOf(previousTotalSteps));
            //Log.d("test", "Total steps: " + event.values[0]);
            countedSteps = (int) totalSteps - (int) previousTotalSteps;
            currentSteps += (countedSteps - previousCountedSteps);
            previousCountedSteps = countedSteps;
            //Log.d("test", "Sensor triggered!");
            updateNotification();
            handler.post(updateBroadcastData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    // Create and show notification
    private void createNotification() {
        // Create notification channel for new Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Pedometer notification",
                    "serviceNotification", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Foreground service notification");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Open app on notification click
        Intent myIntent = new Intent(this, StepActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        builder = new NotificationCompat.Builder(getApplicationContext(), "Pedometer notification")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Your current step count:")
                .setContentText(String.valueOf(currentSteps))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID_PEDOMETER, builder.build());
        //Log.d("test", "Notification shown!");
    }

    private void updateNotification() {
        builder.setContentText(String.valueOf(currentSteps));
        notificationManager.notify(1, builder.build());
        Log.d("test", "Notification updated! (Steps: " + currentSteps + ")");
    }

    private void dismissNotification() {
        notificationManager.cancel(1);
        //Log.d("test", "Notification dismissed!");
    }
}
