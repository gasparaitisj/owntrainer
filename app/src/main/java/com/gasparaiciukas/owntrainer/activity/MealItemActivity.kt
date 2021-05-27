package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.FoodAdapter
import com.gasparaiciukas.owntrainer.database.Food
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.ActivityMealItemBinding
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import io.realm.Realm
import java.util.*
import kotlin.math.roundToInt

class MealItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealItemBinding
    // Pie chart
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

    // Recycler view
    private lateinit var adapter: FoodAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meal_item)

        // Get selected food position
        position = intent.getIntExtra("position", 0)

        // Recycler view
        realm = Realm.getDefaultInstance()
        val meals: List<Meal> = realm.where(Meal::class.java).findAll()
        val foodList: List<Food> = meals[position].foodList
        realm.close()
        adapter = FoodAdapter(foodList)
        layoutManager = LinearLayoutManager(this)
        binding.mealItemRecyclerView.adapter = adapter
        binding.mealItemRecyclerView.layoutManager = layoutManager


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
        binding.mealItemTitle.text = meals[position].title
        binding.mealItemInstructions.text = meals[position].instructions
        binding.mealItemInstructions.movementMethod = ScrollingMovementMethod()
        binding.mealItemCarbsWeight.text = carbs.roundToInt().toString()
        binding.mealItemCarbsPercentage.text =
            String.format("%s %%", (carbs / carbsDailyIntake * 100).roundToInt())
        binding.mealItemFatWeight.text = fat.roundToInt().toString()
        binding.mealItemFatPercentage.text =
            String.format("%s %%", (fat / fatDailyIntake * 100).roundToInt())
        binding.mealItemProteinWeight.text = protein.roundToInt().toString()
        binding.mealItemProteinPercentage.text =
            String.format("%s %%", (protein / proteinDailyIntake * 100).roundToInt())
        binding.mealItemCaloriesCount.text = calories.roundToInt().toString()
        binding.mealItemCaloriesPercentage.text =
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
        binding.mealItemPieChart.data = pieData
        binding.mealItemPieChart.centerText = "${calories.roundToInt()}\nkCal" // calorie text inside inner circle
        binding.mealItemPieChart.setCenterTextSize(14f)
        binding.mealItemPieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        binding.mealItemPieChart.centerTextRadiusPercent = 100f
        binding.mealItemPieChart.setHoleColor(ContextCompat.getColor(this, R.color.colorRed))
        binding.mealItemPieChart.holeRadius = 30f
        binding.mealItemPieChart.transparentCircleRadius = 0f
        binding.mealItemPieChart.legend.isEnabled = false
        binding.mealItemPieChart.description.isEnabled = false
        binding.mealItemPieChart.setTouchEnabled(false)
        binding.mealItemPieChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        adapter.reload()
    }
}