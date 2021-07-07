package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.FragmentFoodItemBinding
import com.gasparaiciukas.owntrainer.network.FoodApi
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import io.realm.Realm
import java.util.*
import kotlin.math.roundToInt

class FoodItemFragment : Fragment() {
    private var _binding: FragmentFoodItemBinding? = null
    private val binding get() = _binding!!

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

    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodItemBinding.inflate(inflater, container, false)
        fetchData()
        initUi()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchData() {
        // Get clicked item position
        foodItem = requireArguments().getParcelable("foodItem")!!

        binding.btnAddToMeal.setOnClickListener {
            val fragment = SelectMealItemFragment()
            val args = Bundle()
            args.putParcelable("foodItem", foodItem)
            args.putInt("quantity", binding.etWeight.text.toString().toInt())
            fragment.arguments = args

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_frame_fragment, fragment)
                .commit()
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
        binding.tvTitle.text = foodItem.label
        binding.tvCarbsWeight.text = carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format("%s %%", (carbs / carbsDailyIntake * 100).roundToInt())
        binding.tvFatWeight.text = fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format("%s %%", (fat / fatDailyIntake * 100).roundToInt())
        binding.tvProteinWeight.text = protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format("%s %%", (protein / proteinDailyIntake * 100).roundToInt())
        binding.tvCaloriesCount.text = calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format("%s %%", (calories / calorieDailyIntake * 100).roundToInt())

        // Create colors representing nutrients
        val colors: MutableList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorGold)) // carbs
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorOrange)) // fat
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorSmokeDark)) // protein


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
        binding.pieChart.data = pieData
        binding.pieChart.centerText =
            "${calories.roundToInt()}\nkCal" // calorie text inside inner circle
        binding.pieChart.setCenterTextSize(14f)
        binding.pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        binding.pieChart.centerTextRadiusPercent = 100f
        binding.pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
        binding.pieChart.holeRadius = 30f
        binding.pieChart.transparentCircleRadius = 0f
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setTouchEnabled(false)
        binding.pieChart.invalidate()
    }
}