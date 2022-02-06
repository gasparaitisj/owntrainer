package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.databinding.FragmentMealItemBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.gasparaiciukas.owntrainer.viewmodel.MealItemViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class MealItemFragment : Fragment() {
    private var _binding: FragmentMealItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DatabaseFoodAdapter

    private val viewModel by viewModels<MealItemViewModel>()

    private val singleClickListener: (food: FoodEntryParcelable) -> Unit = { food: FoodEntryParcelable ->
        val action =
            MealItemFragmentDirections.actionMealItemFragmentToDatabaseFoodItemFragment(food)
        findNavController().navigate(action)
    }

    private val longClickListener: (position: Int) -> Unit = { position: Int ->
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteFoodFromMeal(viewModel.mealWithFoodEntries.foodEntries[position].foodEntryId)
            viewModel.loadData()
            adapter.submitFoodEntries(viewModel.mealWithFoodEntries.foodEntries)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.ldUser.observe(viewLifecycleOwner) {
            viewModel.user = it
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadData()
                initUi()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        initRecyclerView()
        initTextViews()
        initPieChart()
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.topAppBar.title = viewModel.mealWithFoodEntries.meal.title
    }

    private fun initTextViews() {
        binding.tvInstructions.text = viewModel.mealWithFoodEntries.meal.instructions
        binding.tvInstructions.movementMethod = ScrollingMovementMethod()
        binding.tvCarbsWeight.text =
            viewModel.mealWithFoodEntries.meal.carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format(
                "%s %%",
                (viewModel.mealWithFoodEntries.meal.carbs / viewModel.user.dailyCarbsIntakeInG * 100).roundToInt()
            )
        binding.tvFatWeight.text = viewModel.mealWithFoodEntries.meal.fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format(
                "%s %%",
                (viewModel.mealWithFoodEntries.meal.fat / viewModel.user.dailyFatIntakeInG * 100).roundToInt()
            )
        binding.tvProteinWeight.text =
            viewModel.mealWithFoodEntries.meal.protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format(
                "%s %%",
                (viewModel.mealWithFoodEntries.meal.protein / viewModel.user.dailyProteinIntakeInG * 100).roundToInt()
            )
        binding.tvCaloriesCount.text =
            viewModel.mealWithFoodEntries.meal.calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format(
                "%s %%",
                (viewModel.mealWithFoodEntries.meal.calories / viewModel.user.dailyKcalIntake * 100).roundToInt()
            )

        binding.btnAddFood.setOnClickListener {
            val action = MealItemFragmentDirections.actionMealItemFragmentToFoodFragment()
            findNavController().navigate(action)
        }
    }

    private fun initRecyclerView() {
        adapter = DatabaseFoodAdapter(
            viewModel.mealWithFoodEntries.foodEntries,
            singleClickListener,
            longClickListener
        )
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
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
            "${viewModel.mealWithFoodEntries.meal.calories.roundToInt()}\nkCal" // calorie text inside inner circle
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