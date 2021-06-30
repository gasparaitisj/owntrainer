package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.ActivitySettingsBinding
import io.realm.Realm
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    // Variables
    private lateinit var sex: String
    private var age = 0
    private var height = 0
    private var weight = 0.0
    private lateinit var lifestyle: String

    // Database
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appBar.setNavigationOnClickListener { onBackPressed() }
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        writeUserToDatabase()
    }

    private fun writeUserToDatabase() {
        // Write to database
        realm = Realm.getDefaultInstance()
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
        realm.executeTransaction { r -> r.insertOrUpdate(user) }
        realm.close()
    }

    private fun loadData() {
        realm = Realm.getDefaultInstance()
        val user = realm.where(User::class.java)
            .equalTo("userId", "user")
            .findFirst()
        if (user != null) {
            sex = user.sex
            age = user.ageInYears
            height = user.heightInCm
            weight = user.weightInKg
            lifestyle = user.lifestyle
        }
        realm.close()

        // Insert current data into fields
        binding.etSex.setText(sex)
        binding.etAge.setText(age.toString())
        binding.etHeight.setText(height.toString())
        binding.etWeight.setText(weight.toString())
        binding.etLifestyle.setText(lifestyle)

        // Set up listeners
        val sexList: List<String?> = ArrayList(listOf("Male", "Female"))
        val sexAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.details_list_item, sexList)
        binding.etSex.setAdapter(sexAdapter)
        binding.etSex.onItemClickListener =
            OnItemClickListener { _, _, _, _ -> sex = binding.etSex.text.toString() }
        binding.etAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) age = s.toString().toInt()
            }
        })
        binding.etHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) height = s.toString().toInt()
            }
        })
        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) weight = s.toString().toDouble()
            }
        })
        binding.etLifestyle.onItemClickListener =
            OnItemClickListener { _, _, _, _ -> lifestyle = binding.etLifestyle.text.toString()
        }
        val lifestyleList: List<String?> = ArrayList(
            listOf(
                "Sedentary",
                "Lightly active",
                "Moderately active",
                "Very active",
                "Extra active"
            )
        )
        val lifestyleAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.details_list_item, lifestyleList)
        binding.etLifestyle.setAdapter(lifestyleAdapter)
    }
}