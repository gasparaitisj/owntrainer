package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentDatabaseFoodItemBinding
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.gasparaiciukas.owntrainer.viewmodel.DatabaseFoodItemViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class DatabaseFoodItemFragment : Fragment() {
    private var _binding: FragmentDatabaseFoodItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DatabaseFoodItemViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatabaseFoodItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.ldUser.observe(viewLifecycleOwner) {
            viewModel.user = it
            viewModel.loadData()
            initUi()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        initTextViews()
        initPieChart()
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.topAppBar.title = viewModel.food.title
    }

    private fun initPieChart() {
        // Create colors representing nutrients
        val colors: MutableList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorGold)) // carbs
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorOrange)) // fat
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorSmoke)) // protein


        // Add data to pie chart
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry(viewModel.carbsPercentage, "Carbohydrates"))
        entries.add(PieEntry(viewModel.fatPercentage, "Fat"))
        entries.add(PieEntry(viewModel.proteinPercentage, "Protein"))
        val pieDataSet = PieDataSet(entries, "Data")

        // Add style to pie chart
        pieDataSet.colors = colors // chart colors
        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(NutrientValueFormatter()) // adjust labels
        pieData.setValueTextSize(12f)
        binding.pieChart.data = pieData
        binding.pieChart.centerText =
            "${viewModel.food.calories.roundToInt()}\nkCal" // calorie text inside inner circle
        binding.pieChart.setCenterTextSize(14f)
        binding.pieChart.setCenterTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )
        binding.pieChart.centerTextRadiusPercent = 100f
        binding.pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
        binding.pieChart.holeRadius = 30f
        binding.pieChart.transparentCircleRadius = 0f
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setTouchEnabled(false)
        binding.pieChart.invalidate()
    }

    private fun initTextViews() {
        binding.tvQuantityCount.text = viewModel.food.quantityInG.toString()
        binding.tvCarbsWeight.text = viewModel.food.carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format(
                "%s %%",
                (viewModel.food.carbs / viewModel.carbsDailyIntake * 100).roundToInt()
            )
        binding.tvFatWeight.text = viewModel.food.fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format(
                "%s %%",
                (viewModel.food.fat / viewModel.fatDailyIntake * 100).roundToInt()
            )
        binding.tvProteinWeight.text = viewModel.food.protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format(
                "%s %%",
                (viewModel.food.protein / viewModel.proteinDailyIntake * 100).roundToInt()
            )
        binding.tvCaloriesCount.text = viewModel.food.calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format(
                "%s %%",
                (viewModel.food.calories / viewModel.calorieDailyIntake * 100).roundToInt()
            )
    }

    private fun slideBottomNavigationUp() {
        val botNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val layoutParams = botNav?.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = layoutParams.behavior
            if (behavior is HideBottomViewOnScrollBehavior) {
                behavior.slideUp(botNav)
            }
        }
    }

    private fun slideBottomNavigationDown() {
        val botNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val layoutParams = botNav?.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = layoutParams.behavior
            if (behavior is HideBottomViewOnScrollBehavior) {
                behavior.slideDown(botNav)
            }
        }
    }
}