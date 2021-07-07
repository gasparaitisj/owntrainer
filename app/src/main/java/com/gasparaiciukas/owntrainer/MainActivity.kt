package com.gasparaiciukas.owntrainer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gasparaiciukas.owntrainer.databinding.ActivityMainBinding
import com.gasparaiciukas.owntrainer.fragment.*
import io.realm.Realm
import io.realm.RealmConfiguration
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initDatabase()
        initAppIntroThread()
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        // Settings (top app bar)
        binding.appBar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.top_app_bar_settings) {
                val fragment = SettingsFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.layout_frame_fragment, fragment)
                    .commit()
                return@setOnMenuItemClickListener true
            }
            false
        }

        // Bottom navigation bar
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            val selectedFragment: Fragment
            val selectedFragmentTag: String
            when (item.itemId) {
                R.id.navbar_item_1 -> {
                    selectedFragment = DiaryFragment()
                    selectedFragmentTag = "DIARY_FRAGMENT"
                }
                R.id.navbar_item_2 -> {
                    selectedFragment = StepFragment()
                    selectedFragmentTag = "STEP_FRAGMENT"
                    //this.startActivity(Intent(this, StepActivity::class.java))
                    //return@setOnNavigationItemSelectedListener true
                }
                R.id.navbar_item_3 -> {
                    selectedFragment = FoodFragment()
                    selectedFragmentTag = "FOOD_FRAGMENT"
                }
                R.id.navbar_item_4 -> {
                    selectedFragment = ProgressFragment()
                    selectedFragmentTag = "PROGRESS_FRAGMENT"
                }
                else -> return@setOnNavigationItemSelectedListener false
            }

            // Show selected fragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_frame_fragment, selectedFragment, selectedFragmentTag)
            transaction.commit()
            true
        }
    }

    private fun initAppIntroThread() {
        // Start app intro, if app launched for the first time
        val t = Thread {
            val sharedPref = getSharedPreferences("diary", Context.MODE_PRIVATE)
            val isFirstStart = sharedPref.getBoolean("firstStart", true)
            if (isFirstStart) {
                // Diary
                val editor = sharedPref.edit()
                editor.putBoolean("firstStart", false)
                editor.putInt("year", LocalDate.now().year)
                editor.putInt("month", LocalDate.now().monthValue)
                editor.putInt("day", LocalDate.now().dayOfMonth)
                editor.apply()
                val i = Intent(this@MainActivity, IntroActivity::class.java)
                runOnUiThread { startActivity(i) }
            } else {
                // Show home fragment
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(
                        R.id.layout_frame_fragment,
                        DiaryFragment(),
                        "DIARY_FRAGMENT")
                transaction.commit()
            }
        }
        t.start()
    }

    private fun initDatabase() {
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }
}