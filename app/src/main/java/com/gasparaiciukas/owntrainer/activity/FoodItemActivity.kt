package com.gasparaiciukas.owntrainer.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.network.FoodApi
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm
import java.util.*
import kotlin.math.roundToInt

class FoodItemActivity : AppCompatActivity() {
    // Data
    private var position = 0
    private var carbs = 0f
    private var carbsPercentage = 0f
    private var carbsDailyIntake = 0f
    private var calories = 0f
    private var calorieDailyIntake = 0f
    private var fat = 0f
    private var fatPercentage = 0f
    private var fatDailyIntake = 0f
    private var protein = 0f
    private var proteinPercentage = 0f
    private var proteinDailyIntake = 0f
    private lateinit var foodItem: FoodApi

    // Pie chart
    private lateinit var pieChart: PieChart
    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData

    // Text views
    private lateinit var tFoodTitle: TextView
    private lateinit var tCarbsWeight: TextView
    private lateinit var tCarbsDailyIntake: TextView
    private lateinit var tFatWeight: TextView
    private lateinit var tFatDailyIntake: TextView
    private lateinit var tProteinWeight: TextView
    private lateinit var tProteinDailyIntake: TextView
    private lateinit var tCalorieCount: TextView
    private lateinit var tCalorieDailyIntake: TextView
    private lateinit var addToMealButton: Button
    private lateinit var inputFoodWeight: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item)
        getViews()
        fetchData()
        initUi()
    }

    private fun getViews() {
        pieChart = findViewById(R.id.food_pie_chart)
        tCarbsWeight = findViewById(R.id.food_carbs_weight)
        tCarbsDailyIntake = findViewById(R.id.food_carbs_percentage)
        tFatWeight = findViewById(R.id.food_fat_weight)
        tFatDailyIntake = findViewById(R.id.food_fat_percentage)
        tProteinWeight = findViewById(R.id.food_protein_weight)
        tProteinDailyIntake = findViewById(R.id.food_protein_percentage)
        tCalorieCount = findViewById(R.id.food_calories_count)
        tCalorieDailyIntake = findViewById(R.id.food_calories_percentage)
        tFoodTitle = findViewById(R.id.food_item_title)
        addToMealButton = findViewById(R.id.food_add_to_meal_button)
        inputFoodWeight = findViewById(R.id.food_weight_input_text)
    }
    private fun fetchData() {
        // Get clicked item position
        foodItem = intent.getParcelableExtra("foodItem")!!

        addToMealButton.setOnClickListener {
            val intent = Intent(baseContext, SelectMealItemActivity::class.java)
            intent.putExtra("foodItem", foodItem)
            intent.putExtra("quantity", inputFoodWeight.text.toString().toInt())
            startActivity(intent)
        }

        // Get nutrients from food item
        val nutrients = foodItem.nutrients
        carbs = nutrients.carbs.toFloat()
        calories = nutrients.calories.toFloat()
        fat = nutrients.fat.toFloat()
        protein = nutrients.protein.toFloat()

        // Calculate percentage of each item
        val sum = carbs + fat + protein
        carbsPercentage = carbs / sum * 100
        fatPercentage = fat / sum * 100
        proteinPercentage = protein / sum * 100

        // Get daily intake percentages
        val realm = Realm.getDefaultInstance()
        val user = realm.where(User::class.java)
            .equalTo("userId", "user")
            .findFirst()
        if (user != null) {
            calorieDailyIntake = user.dailyKcalIntake.toFloat()
            carbsDailyIntake = user.dailyCarbsIntakeInG.toFloat()
            fatDailyIntake = user.dailyFatIntakeInG.toFloat()
            proteinDailyIntake = user.dailyProteinIntakeInG.toFloat()
        }
        realm.close()
    }

    private fun initUi() {
        // Set up text views
        tFoodTitle.text = foodItem.label
        tCarbsWeight.text = carbs.roundToInt().toString()
        tCarbsDailyIntake.text = String.format("%s %%", (carbs / carbsDailyIntake * 100).roundToInt())
        tFatWeight.text = fat.roundToInt().toString()
        tFatDailyIntake.text = String.format("%s %%", (fat / fatDailyIntake * 100).roundToInt())
        tProteinWeight.text = protein.roundToInt().toString()
        tProteinDailyIntake.text =
            String.format("%s %%", (protein / proteinDailyIntake * 100).roundToInt())
        tCalorieCount.text = calories.roundToInt().toString()
        tCalorieDailyIntake.text =
            String.format("%s %%", (calories / calorieDailyIntake * 100).roundToInt())

        // Create colors representing nutrients
        val colors: MutableList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(this, R.color.colorGold)) // carbs
        colors.add(ContextCompat.getColor(this, R.color.colorOrange)) // fat
        colors.add(ContextCompat.getColor(this, R.color.colorSmokeDark)) // protein


        // Add data to pie chart
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry(carbsPercentage, "Carbohydrates"))
        entries.add(PieEntry(fatPercentage, "Fat"))
        entries.add(PieEntry(proteinPercentage, "Protein"))
        pieDataSet = PieDataSet(entries, "Data")

        // Add style to pie chart
        pieDataSet.colors = colors // chart colors
        pieData = PieData(pieDataSet)
        pieData.setValueFormatter(NutrientValueFormatter()) // adjust labels
        pieData.setValueTextSize(12f)
        pieChart.data = pieData
        pieChart.centerText = "${calories.roundToInt()}\nkCal" // calorie text inside inner circle
        pieChart.setCenterTextSize(14f)
        pieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        pieChart.centerTextRadiusPercent = 100f
        pieChart.setHoleColor(ContextCompat.getColor(this, R.color.colorRed))
        pieChart.holeRadius = 30f
        pieChart.transparentCircleRadius = 0f
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.invalidate()
    }
}