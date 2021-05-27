package com.gasparaiciukas.owntrainer.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.ActivityFoodItemBinding
import com.gasparaiciukas.owntrainer.network.FoodApi
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import io.realm.Realm
import java.util.*
import kotlin.math.roundToInt

class FoodItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodItemBinding
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
    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_item)
        fetchData()
        initUi()
    }
    private fun fetchData() {
        // Get clicked item position
        foodItem = intent.getParcelableExtra("foodItem")!!

        binding.foodAddToMealButton.setOnClickListener {
            val intent = Intent(baseContext, SelectMealItemActivity::class.java)
            intent.putExtra("foodItem", foodItem)
            intent.putExtra("quantity", binding.foodWeightInputText.text.toString().toInt())
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
        binding.foodItemTitle.text = foodItem.label
        binding.foodCarbsWeight.text = carbs.roundToInt().toString()
        binding.foodCarbsPercentage.text = String.format("%s %%", (carbs / carbsDailyIntake * 100).roundToInt())
        binding.foodFatWeight.text = fat.roundToInt().toString()
        binding.foodFatPercentage.text = String.format("%s %%", (fat / fatDailyIntake * 100).roundToInt())
        binding.foodProteinWeight.text = protein.roundToInt().toString()
        binding.foodProteinPercentage.text =
            String.format("%s %%", (protein / proteinDailyIntake * 100).roundToInt())
        binding.foodCaloriesCount.text = calories.roundToInt().toString()
        binding.foodCaloriesPercentage.text =
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
        binding.foodPieChart.data = pieData
        binding.foodPieChart.centerText = "${calories.roundToInt()}\nkCal" // calorie text inside inner circle
        binding.foodPieChart.setCenterTextSize(14f)
        binding.foodPieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        binding.foodPieChart.centerTextRadiusPercent = 100f
        binding.foodPieChart.setHoleColor(ContextCompat.getColor(this, R.color.colorRed))
        binding.foodPieChart.holeRadius = 30f
        binding.foodPieChart.transparentCircleRadius = 0f
        binding.foodPieChart.legend.isEnabled = false
        binding.foodPieChart.description.isEnabled = false
        binding.foodPieChart.setTouchEnabled(false)
        binding.foodPieChart.invalidate()
    }
}