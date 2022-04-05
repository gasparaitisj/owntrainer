package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.databinding.FragmentMealItemBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.gasparaiciukas.owntrainer.viewmodel.MealItemUiState
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
class MealItemFragment : Fragment(R.layout.fragment_meal_item) {
    private var _binding: FragmentMealItemBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: DatabaseFoodAdapter

    lateinit var viewModel: MealItemViewModel

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
        viewModel = ViewModelProvider(this)[MealItemViewModel::class.java]
        viewModel.ldUser.observe(viewLifecycleOwner) {
            viewModel.loadData(it)
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            refreshUi(it)
        }
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        initRecyclerView()
    }

    private fun refreshUi(uiState: MealItemUiState) {
        setTextViews(uiState)
        setPieChart(uiState)
        adapter.items = uiState.mealWithFoodEntries.foodEntries
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAddFood.setOnClickListener {
            findNavController().navigate(
                MealItemFragmentDirections.actionMealItemFragmentToFoodFragment()
            )
        }
    }

    private fun setTextViews(uiState: MealItemUiState) {
        binding.topAppBar.title = uiState.mealWithFoodEntries.meal.title
        binding.tvInstructions.text = uiState.mealWithFoodEntries.meal.instructions
        binding.tvInstructions.movementMethod = ScrollingMovementMethod()
        binding.tvCarbsWeight.text =
            uiState.mealWithFoodEntries.meal.carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format(
                "%s %%",
                uiState.carbsDailyIntakePercentage.roundToInt()
            )
        binding.tvFatWeight.text = uiState.mealWithFoodEntries.meal.fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format(
                "%s %%",
                uiState.fatDailyIntakePercentage.roundToInt()
            )
        binding.tvProteinWeight.text =
            uiState.mealWithFoodEntries.meal.protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format(
                "%s %%",
                uiState.proteinDailyIntakePercentage.roundToInt()
            )
        binding.tvCaloriesCount.text =
            uiState.mealWithFoodEntries.meal.calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format(
                "%s %%",
                uiState.caloriesDailyIntakePercentage.roundToInt()
            )
    }

    private fun initRecyclerView() {
        adapter = DatabaseFoodAdapter()
        adapter.setOnClickListeners(
            singleClickListener = { food: FoodEntryParcelable ->
                findNavController().navigate(
                    MealItemFragmentDirections.actionMealItemFragmentToDatabaseFoodItemFragment(food)
                )
            },
            longClickListener = { food: FoodEntry ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteFoodFromMeal(food.foodEntryId)
                }
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun setPieChart(uiState: MealItemUiState) {
        // Colors representing their respective nutrient
        val colors: MutableList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorGold)) // carbs
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorOrange)) // fat
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorSmoke)) // protein

        // Data chart labels
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry(uiState.carbsPercentage.toFloat(), "Carbohydrates"))
        entries.add(PieEntry(uiState.fatPercentage.toFloat(), "Fat"))
        entries.add(PieEntry(uiState.proteinPercentage.toFloat(), "Protein"))
        val pieDataSet = PieDataSet(entries, "Data")
        pieDataSet.colors = colors // chart colors
        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(NutrientValueFormatter()) // adjust labels
        pieData.setValueTextSize(12f)

        // Center text customization
        binding.pieChart.setCenterTextSize(14f)
        binding.pieChart.setCenterTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )
        binding.pieChart.centerTextRadiusPercent = 100f

        // Other customization
        binding.pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
        binding.pieChart.holeRadius = 30f
        binding.pieChart.transparentCircleRadius = 0f
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setTouchEnabled(false)

        // Set and invalidate current data
        binding.pieChart.data = pieData
        binding.pieChart.centerText =
            "${uiState.mealWithFoodEntries.meal.calories.roundToInt()}\nkCal"

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