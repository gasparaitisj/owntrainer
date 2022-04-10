package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.databinding.FragmentMealItemBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.viewmodel.MealItemUiState
import com.gasparaiciukas.owntrainer.viewmodel.MealItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MealItemFragment : Fragment(R.layout.fragment_meal_item) {
    private var _binding: FragmentMealItemBinding? = null
    private val binding get() = _binding!!

    lateinit var databaseFoodAdapter: DatabaseFoodAdapter

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
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        initRecyclerView()
    }

    private fun refreshUi(uiState: MealItemUiState) {
        setTextViews(uiState)
        setPieChart(
            carbsPercentage = uiState.carbsPercentage,
            fatPercentage = uiState.fatPercentage,
            proteinPercentage = uiState.proteinPercentage,
            calories = uiState.mealWithFoodEntries.calories.toFloat(),
            pieChart = binding.pieChart,
            context = requireContext()
        )
        databaseFoodAdapter.items = uiState.mealWithFoodEntries.foodEntries
        binding.scrollView.visibility = View.VISIBLE
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
        databaseFoodAdapter = DatabaseFoodAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = databaseFoodAdapter
        }
        databaseFoodAdapter.setOnClickListeners(
            singleClickListener = { food: FoodEntryParcelable ->
                findNavController().navigate(
                    MealItemFragmentDirections.actionMealItemFragmentToDatabaseFoodItemFragment(food)
                )
            },
            longClickListener = { food: FoodEntry ->
                viewModel.deleteFoodFromMeal(food.foodEntryId)
            }
        )
    }
}