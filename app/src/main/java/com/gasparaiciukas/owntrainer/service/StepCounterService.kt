package com.gasparaiciukas.owntrainer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gasparaiciukas.owntrainer.R

class StepCounterService : Service(), SensorEventListener {
    // Variables
    private var totalSteps: Float = 0f
    private var previousTotalSteps: Float = 0f
    private var currentSteps: Int = 0
    private var countedSteps: Int = 0
    private var previousCountedSteps: Int = 0

    // Sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var stepSensor: Sensor

    // Broadcasting
    private lateinit var broadcastIntent: Intent
    private val handler = Handler(Looper.getMainLooper())

    // Notification
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var builder: NotificationCompat.Builder

    override fun onCreate() {
        broadcastIntent = Intent(BROADCAST_PEDOMETER)
        //Log.d("test", "Service created!");
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Load data
        currentSteps = intent.getIntExtra("currentSteps", 0)

        // Create and show notification
        createNotification()

        // Set up sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)

        // Set up handler
        handler.removeCallbacks(updateBroadcastData)
        //handler.post(updateBroadcastData);
        return START_STICKY
    }

    // Runnable for updating broadcast data
    private val updateBroadcastData = Runnable {
        // Send counted steps
        broadcastIntent.putExtra("countedSteps", countedSteps)
        sendBroadcast(broadcastIntent)

        //Log.d("test", "Counted " + countedSteps + " steps.");
    }

    // Stop service
    override fun onDestroy() {
        super.onDestroy()
        dismissNotification()
        sensorManager.unregisterListener(this, stepSensor)
        handler.removeCallbacks(updateBroadcastData)
        //Log.d("test", "Service destroyed.");
    }

    override fun onBind(intent: Intent): IBinder? {
        // binding not implemented
        return null
    }

    // Broadcast counted steps and update notification
    override fun onSensorChanged(event: SensorEvent) {
        if (previousTotalSteps == 0f) {
            previousTotalSteps = event.values[0]
        } else {
            totalSteps = event.values[0]
            //Log.d("test", "Previous total steps: " + String.valueOf(previousTotalSteps));
            //Log.d("test", "Total steps: " + event.values[0]);
            countedSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            currentSteps += (countedSteps - previousCountedSteps)
            previousCountedSteps = countedSteps
            //Log.d("test", "Sensor triggered!");
            updateNotification()
            handler.post(updateBroadcastData)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // do nothing
    }

    // Create and show notification
    private fun createNotification() {
        // Create notification channel for new Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    "Pedometer notification",
                    "serviceNotification",
                    NotificationManager.IMPORTANCE_LOW)
            channel.description = "Foreground service notification"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Open app on notification click
        //val stepActivityIntent = Intent(this, StepActivity::class.java)
        //val pendingIntent = PendingIntent.getActivity(this, 1, stepActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Build notification
        builder = NotificationCompat.Builder(applicationContext, "Pedometer notification")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Your current step count:")
                .setContentText(currentSteps.toString())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                //.setContentIntent(pendingIntent)
        notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID_PEDOMETER, builder.build())
        //Log.d("test", "Notification shown!");
    }

    private fun updateNotification() {
        builder.setContentText(currentSteps.toString())
        notificationManager.notify(1, builder.build())
        Log.d("test", "Notification updated! (Steps: $currentSteps)")
    }

    private fun dismissNotification() {
        notificationManager.cancel(1)
        //Log.d("test", "Notification dismissed!");
    }

    companion object {
        const val BROADCAST_PEDOMETER = "Pedometer notification"
        private const val NOTIFICATION_ID_PEDOMETER = 1
    }
}