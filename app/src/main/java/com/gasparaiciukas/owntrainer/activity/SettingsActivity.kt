package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm
import java.util.*

class SettingsActivity : AppCompatActivity() {
    // Variables
    private lateinit var sex: String
    private var age = 0
    private var height = 0
    private var weight = 0.0
    private lateinit var lifestyle: String

    // Text views
    private lateinit var sexMenu: AutoCompleteTextView
    private lateinit var tAge: TextInputEditText
    private lateinit var tHeight: TextInputEditText
    private lateinit var tWeight: TextInputEditText
    private lateinit var lifestyleMenu: AutoCompleteTextView

    // Database
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initUi()
        loadData()
    }

    private fun initUi() {
        // Back button
        val toolbar = findViewById<MaterialToolbar>(R.id.settings_top_app_bar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Options
        sexMenu = findViewById(R.id.settings_sex_menu)
        tAge = findViewById(R.id.settings_age_text)
        tHeight = findViewById(R.id.settings_height_text)
        tWeight = findViewById(R.id.settings_weight_text)
        lifestyleMenu = findViewById(R.id.settings_lifestyle_menu)
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
        sexMenu.setText(sex)
        tAge.setText(age.toString())
        tHeight.setText(height.toString())
        tWeight.setText(weight.toString())
        lifestyleMenu.setText(lifestyle)

        // Set up listeners
        val sexList: List<String?> = ArrayList(listOf("Male", "Female"))
        val sexAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.details_list_item, sexList)
        sexMenu.setAdapter(sexAdapter)
        sexMenu.onItemClickListener =
            OnItemClickListener { _, _, _, _ -> sex = sexMenu.text.toString() }
        tAge.addTextChangedListener(object : TextWatcher {
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
        tHeight.addTextChangedListener(object : TextWatcher {
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
        tWeight.addTextChangedListener(object : TextWatcher {
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
        lifestyleMenu.onItemClickListener = OnItemClickListener { _, _, _, _ ->
            lifestyle = lifestyleMenu.text.toString()
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
        lifestyleMenu.setAdapter(lifestyleAdapter)
    }
}