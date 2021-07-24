package com.gasparaiciukas.owntrainer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.RealmConfiguration
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val appIntroLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //TODO: Reload fragment after app intro is finished
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initDatabase()
        initAppIntro()
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        // Get NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        // Bottom navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Top app bar
        setSupportActionBar(binding.appBar)
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun initDatabase() {
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)

        // Set default database values, if app launched for the first time
        val sharedPref = getSharedPreferences("diary", Context.MODE_PRIVATE)
        val isFirstStart = sharedPref.getBoolean("firstStart", true)
        if (isFirstStart) {
            sharedPref.edit()
                .putInt("year", LocalDate.now().year)
                .putInt("month", LocalDate.now().monthValue)
                .putInt("day", LocalDate.now().dayOfMonth)
                .apply()

            val realmInstance = Realm.getDefaultInstance()
            val sex = "Male"
            val age = 25
            val height = 180
            val weight = 80.0
            val lifestyle = "Lightly active"
            val user = User()

            user.userId = "user"
            user.ageInYears = age
            user.sex = sex
            user.heightInCm = height
            user.weightInKg = weight
            user.lifestyle = lifestyle
            user.stepLengthInCm = user.calculateStepLengthInCm(height.toDouble(), sex)
            user.bmr = user.calculateBmr(weight, height.toDouble(), age, sex)
            user.kcalBurnedPerStep =
                user.calculateKcalBurnedPerStep(weight, height.toDouble(), user.avgWalkingSpeedInKmH)
            user.dailyKcalIntake = user.calculateDailyKcalIntake(user.bmr, lifestyle)
            user.dailyCarbsIntakeInG = user.calculateDailyCarbsIntake(user.dailyKcalIntake)
            user.dailyFatIntakeInG = user.calculateDailyFatIntake(user.dailyKcalIntake)
            user.dailyProteinIntakeInG = user.calculateDailyProteinIntakeInG(weight)

            realmInstance.executeTransaction { realm -> realm.insertOrUpdate(user) }
            realmInstance.close()
        }
    }

    private fun initAppIntro() {
        // Start app intro, if app launched for the first time
        val sharedPref = getSharedPreferences("diary", Context.MODE_PRIVATE)
        val isFirstStart = sharedPref.getBoolean("firstStart", true)
        if (isFirstStart) {
            // Diary
            sharedPref.edit().apply {
                putInt("year", LocalDate.now().year)
                putInt("month", LocalDate.now().monthValue)
                putInt("day", LocalDate.now().dayOfMonth)
                apply()
            }
            val intent = Intent(this, IntroActivity::class.java)
            appIntroLauncher.launch(intent)
        }
    }
}