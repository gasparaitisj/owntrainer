package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.FoodAdapter
import com.gasparaiciukas.owntrainer.database.Food
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import io.realm.Realm
import java.util.*
import kotlin.math.roundToInt

class MealItemActivity : AppCompatActivity() {
    // Pie chart
    private lateinit var pieChart: PieChart
    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData

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
    private val quantity = 0

    // Text views
    private lateinit var tMealTitle: TextView
    private lateinit var tMealInstructions: TextView
    private lateinit var tCarbsWeight: TextView
    private lateinit var tCarbsDailyIntake: TextView
    private lateinit var tFatWeight: TextView
    private lateinit var tFatDailyIntake: TextView
    private lateinit var tProteinWeight: TextView
    private lateinit var tProteinDailyIntake: TextView
    private lateinit var tCalorieCount: TextView
    private lateinit var tCalorieDailyIntake: TextView

    // Recycler view
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FoodAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_item)

        // Get selected food position
        position = intent.getIntExtra("position", 0)

        // Views
        pieChart = findViewById(R.id.meal_item_pie_chart)
        tCarbsWeight = findViewById(R.id.meal_item_carbs_weight)
        tCarbsDailyIntake = findViewById(R.id.meal_item_carbs_percentage)
        tFatWeight = findViewById(R.id.meal_item_fat_weight)
        tFatDailyIntake = findViewById(R.id.meal_item_fat_percentage)
        tProteinWeight = findViewById(R.id.meal_item_protein_weight)
        tProteinDailyIntake = findViewById(R.id.meal_item_protein_percentage)
        tCalorieCount = findViewById(R.id.meal_item_calories_count)
        tCalorieDailyIntake = findViewById(R.id.meal_item_calories_percentage)
        tMealTitle = findViewById(R.id.meal_item_title)
        tMealInstructions = findViewById(R.id.meal_item_instructions)
        tMealInstructions.movementMethod = ScrollingMovementMethod()
        recyclerView = findViewById(R.id.meal_item_recycler_view)

        // Recycler view
        realm = Realm.getDefaultInstance()
        val meals: List<Meal> = realm.where(Meal::class.java).findAll()
        val foodList: List<Food> = meals[position].foodList
        realm.close()
        adapter = FoodAdapter(foodList)
        layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager


        // Get nutrients from food item
        carbs = meals[position].calculateCarbs().toFloat()
        calories = meals[position].calculateCalories().toFloat()
        fat = meals[position].calculateFat().toFloat()
        protein = meals[position].calculateProtein().toFloat()

        // Calculate percentage of each item
        val sum = carbs + fat + protein
        carbsPercentage = carbs / sum * 100
        fatPercentage = fat / sum * 100
        proteinPercentage = protein / sum * 100

        // Get daily intake percentages
        realm = Realm.getDefaultInstance()
        val u = realm.where(User::class.java)
            .equalTo("userId", "user")
            .findFirst()

        if (u != null) {
            calorieDailyIntake = u.dailyKcalIntake.toFloat()
            carbsDailyIntake = u.dailyCarbsIntakeInG.toFloat()
            fatDailyIntake = u.dailyFatIntakeInG.toFloat()
            proteinDailyIntake = u.dailyProteinIntakeInG.toFloat()
        }
        realm.close()

        // Set up text views
        tMealTitle.text = meals[position].title
        tMealInstructions.text = meals[position].instructions
        tCarbsWeight.text = carbs.roundToInt().toString()
        tCarbsDailyIntake.text =
            String.format("%s %%", (carbs / carbsDailyIntake * 100).roundToInt())
        tFatWeight.text = fat.roundToInt().toString()
        tFatDailyIntake.text =
            String.format("%s %%", (fat / fatDailyIntake * 100).roundToInt())
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

    override fun onResume() {
        super.onResume()
        adapter.reload()
    }
}