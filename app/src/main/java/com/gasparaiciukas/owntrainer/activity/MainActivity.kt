package com.gasparaiciukas.owntrainer.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.ActivityMainBinding
import com.gasparaiciukas.owntrainer.fragment.DiaryFragment
import com.gasparaiciukas.owntrainer.fragment.FoodFragment
import com.gasparaiciukas.owntrainer.fragment.ProgressFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDatabase()
        initAppIntroThread()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initUi()
    }

    private fun initUi() {
        // Settings (top app bar)
        binding.topAppBar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.top_app_bar_settings) {
                startActivity(Intent(baseContext, SettingsActivity::class.java))
                return@setOnMenuItemClickListener true
            }
            false
        }

        // Bottom navigation bar
        binding.mainBottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            val selectedFragment: Fragment
            val selectedFragmentTag: String
            when (item.itemId) {
                R.id.navbar_item_1 -> {
                    selectedFragment = DiaryFragment.newInstance()
                    selectedFragmentTag = "DIARY_FRAGMENT"
                }
                R.id.navbar_item_2 -> {
                    this.startActivity(Intent(this, StepActivity::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navbar_item_3 -> {
                    selectedFragment = FoodFragment.newInstance()
                    selectedFragmentTag = "FOOD_FRAGMENT"
                }
                R.id.navbar_item_4 -> {
                    selectedFragment = ProgressFragment.newInstance()
                    selectedFragmentTag = "PROGRESS_FRAGMENT"
                }
                else -> return@setOnNavigationItemSelectedListener false
            }

            // Show selected fragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_fragment_frame_layout, selectedFragment, selectedFragmentTag)
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
                        R.id.main_fragment_frame_layout,
                        DiaryFragment.newInstance(),
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