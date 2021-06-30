package com.gasparaiciukas.owntrainer.activity

import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.PedometerEntry
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.ActivityStepBinding
import com.gasparaiciukas.owntrainer.service.StepCounterService
import io.realm.Realm
import java.time.LocalDate

class StepActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStepBinding

    private var currentSteps = 0
    private var countedSteps = 0
    private var previousCountedSteps = 0
    private var calories = 0
    private var distance = 0.0
    private var isStepCounterServiceRunning = false
    private var previousDay = 0
    private var dailyStepGoal = 0
    private var kcalBurnedPerStep = 0.0
    private var stepLengthInCm = 0.0
    private var timeElapsedInS = 0
    private var startTimeInS: Long = 0
    private var stopTimeInS: Long = 0
    private var totalTimeElapsedInS = 0

    private lateinit var serviceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load data
        loadData()
        calories = (currentSteps * kcalBurnedPerStep).toInt()
        distance = currentSteps * (stepLengthInCm / 100000)

        // If it is the next day, reset steps and save that day's steps to database
        //Log.d(TAG, "onCreate: " + previousDay + " " + LocalDate.now().getDayOfYear());
        if (previousDay != LocalDate.now().dayOfYear) saveDay()

        // Set listener to reset steps
        resetStepsOnClick()
        isStepCounterServiceRunning = isServiceRunning
        //Log.d("test", "Service is running:" + isServiceRunning);

        // Show data
        binding.progressBar.progress = currentSteps.toFloat()
        binding.tvStepCountCurrent.text = currentSteps.toString()
        binding.tvCaloriesCount.text = calories.toString()
        binding.tvDistanceCount.text = String.format("%.2f", distance)
        binding.tvStepCountDaily.text = dailyStepGoal.toString()
        binding.tvTimeCountH.text = (totalTimeElapsedInS / 3600).toString()
        binding.tvTimeCountM.text = (totalTimeElapsedInS % 3600 / 60).toString()
        if (isStepCounterServiceRunning) {
            binding.btnStart.setText(R.string.stop)
        } else {
            binding.btnStart.setText(R.string.start)
        }

        // Set up intent
        serviceIntent = Intent(this, StepCounterService::class.java)
        serviceIntent.putExtra("currentSteps", currentSteps)
        binding.btnStart.setOnClickListener {
            if (isStepCounterServiceRunning) onStopButtonClicked()
            else onStartButtonClicked()
        }
    }

    // Set up broadcast receiver to update current step count
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Receive counted steps
            countedSteps = intent.getIntExtra("countedSteps", 0)
            //Log.d("test", "Received " + countedSteps + " steps.");

            // Update counted steps
            currentSteps += countedSteps - previousCountedSteps
            previousCountedSteps = countedSteps
            saveSteps()
            calories = (currentSteps * kcalBurnedPerStep).toInt()
            distance = currentSteps * (stepLengthInCm / 100000)
            binding.progressBar.setProgressWithAnimation(currentSteps.toFloat())
            binding.tvStepCountCurrent.text = currentSteps.toString()
            binding.tvCaloriesCount.text = calories.toString()
            binding.tvDistanceCount.text = String.format("%.2f", distance)
            //Log.d("test", "Bar updated! (Steps: " + currentSteps + ")");
        }
    }

    private fun resetSteps() {
        // Reset data
        currentSteps = 0
        calories = 0
        distance = 0.0
        totalTimeElapsedInS = 0

        // Reset UI
        binding.progressBar.setProgressWithAnimation(currentSteps.toFloat())
        binding.tvStepCountCurrent.text = currentSteps.toString()
        binding.tvCaloriesCount.text = calories.toString()
        binding.tvDistanceCount.text = String.format("%.2f", distance)
        binding.tvTimeCountH.text = (totalTimeElapsedInS / 3600).toString()
        binding.tvTimeCountH.text = (totalTimeElapsedInS % 3600 / 60).toString()
        saveSteps()
        //Log.d("test", "Steps reset!");
    }

    // Resets step counter, when long clicked on step counter
    private fun resetStepsOnClick() {
        binding.progressBar.setOnLongClickListener {
            resetSteps()
            true
        }
    }

    private fun saveSteps() {
        // Save steps to shared preferences
        val sharedPreferences = getSharedPreferences("pedometer", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("currentSteps", currentSteps)
        editor.putInt("totalTimeElapsedInS", totalTimeElapsedInS)
        editor.apply()
        //Log.d("test", "Saved: Current steps: " + String.valueOf(currentSteps));
    }

    private fun saveDay() {
        // Save day to shared preferences
        val sharedPreferences = getSharedPreferences("pedometer", MODE_PRIVATE)
        val isFirstStart = sharedPreferences.getBoolean("isFirstStart", true)
        val editor = sharedPreferences.edit()
        previousDay = LocalDate.now().dayOfYear
        editor.putInt("previousDay", previousDay)
        editor.apply()

        // Save steps of previous day to database
        val realm = Realm.getDefaultInstance()
        val entry = PedometerEntry()
        entry.calories = calories
        entry.dayOfYear = previousDay
        entry.distanceInKm = distance
        entry.steps = currentSteps
        entry.timeElapsedInS = totalTimeElapsedInS
        entry.year = LocalDate.now().year
        entry.yearAndDayOfYear = LocalDate.now().year.toString() + previousDay.toString()
        realm.executeTransaction { r: Realm -> r.insertOrUpdate(entry) }
        //        RealmResults<PedometerEntry> entries = realm.where(PedometerEntry.class).findAll();
//        for (PedometerEntry entry : entries) {
//            Log.d(TAG, entry.getYear() + " " + entry.getDayOfYear() + " " +
//                    entry.getSteps() + " " + entry.gebinding.stepCalorieCount() + " " +
//                    entry.gebinding.stepDistanceCountInKm() + " " + entry.getTimeElapsedInS());
//        }
        realm.close()

        // Handle first start
        if (isFirstStart) {
            editor.putBoolean("isFirstStart", false)
            editor.apply()
        } else {
            resetSteps()
        }
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("pedometer", MODE_PRIVATE)
        currentSteps = sharedPreferences.getInt("currentSteps", 0)
        previousDay = sharedPreferences.getInt("previousDay", 0)
        totalTimeElapsedInS = sharedPreferences.getInt("totalTimeElapsedInS", 0)
        val realm = Realm.getDefaultInstance()
        val u = realm.where(User::class.java)
            .equalTo("userId", "user")
            .findFirst()
        if (u != null) {
            dailyStepGoal = u.dailyStepGoal
            kcalBurnedPerStep = u.kcalBurnedPerStep
            stepLengthInCm = u.stepLengthInCm
        }
        realm.close()
        //Log.d("test", "Loaded: Current steps: " + String.valueOf(currentSteps));
    }

    private fun onStopButtonClicked() {
        // Destroy service and reload activity
        destroyService()
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun onStartButtonClicked() {
        // Ask for physical activity permission if not provided
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    1)
            }
        }

        // Start service
        startService(serviceIntent)
        isStepCounterServiceRunning = true
        registerReceiver(broadcastReceiver, IntentFilter(StepCounterService.BROADCAST_PEDOMETER))
        binding.btnStart.setText(R.string.stop)
        startTimeInS = System.currentTimeMillis() / 1000
    }

    // Stop service and receiver
    private fun destroyService() {
        try {
            // try to unregister receiver
            unregisterReceiver(broadcastReceiver)
        } catch (ignored: IllegalArgumentException) {
            // ignore exception if receiver already unregistered
        }
        stopTimeInS = System.currentTimeMillis() / 1000
        timeElapsedInS = (stopTimeInS - startTimeInS).toInt()
        totalTimeElapsedInS += timeElapsedInS
        stopService(serviceIntent)
        saveSteps()
        isStepCounterServiceRunning = false
        binding.btnStart.setText(R.string.start)
    }

    private val isServiceRunning: Boolean
        get() {
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (StepCounterService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }
}