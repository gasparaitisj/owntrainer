package com.gasparaiciukas.owntrainer.ui.meals.meal

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentMealItemBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.utils.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.utils.database.FoodEntry
import com.gasparaiciukas.owntrainer.utils.fragment.setPieChart
import com.gasparaiciukas.owntrainer.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    when (uiState?.status) {
                        Status.SUCCESS -> {
                            uiState.data?.let { refreshUi(it) }
                        }
                        else -> {}
                    }
                }
            }
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

    fun refreshUi(uiState: MealItemUiState) {
        setTextViews(uiState)
        setPieChart(
            carbsPercentage = uiState.carbsPercentage,
            fatPercentage = uiState.fatPercentage,
            proteinPercentage = uiState.proteinPercentage,
            calories = uiState.mealWithFoodEntries.calories.toFloat(),
            pieChart = binding.cardNutrition.pieChart,
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

        binding.cardNutrition.tvCarbsWeight.text = getString(
            R.string.append_g,
            uiState.mealWithFoodEntries.meal.carbs.roundToInt().toString()
        )

        binding.cardNutrition.tvCarbsPercentage.text = getString(
            R.string.append_percent_sign,
            uiState.carbsDailyIntakePercentage.roundToInt().toString()
        )
        binding.cardNutrition.tvFatWeight.text = getString(
            R.string.append_g,
            uiState.mealWithFoodEntries.meal.fat.roundToInt().toString()
        )
        binding.cardNutrition.tvFatPercentage.text = getString(
            R.string.append_percent_sign,
            uiState.fatDailyIntakePercentage.roundToInt().toString()
        )
        binding.cardNutrition.tvProteinWeight.text = getString(
            R.string.append_g,
            uiState.mealWithFoodEntries.meal.protein.roundToInt().toString()
        )
        binding.cardNutrition.tvProteinPercentage.text = getString(
            R.string.append_percent_sign,
            uiState.proteinDailyIntakePercentage.roundToInt().toString()
        )
        binding.cardNutrition.tvCaloriesWeight.text = getString(
            R.string.append_kcal,
            uiState.mealWithFoodEntries.meal.calories.roundToInt().toString()
        )
        binding.cardNutrition.tvCaloriesPercentage.text = getString(
            R.string.append_g,
            uiState.caloriesDailyIntakePercentage.roundToInt().toString()
        )
    }

    private fun initRecyclerView() {
        databaseFoodAdapter = DatabaseFoodAdapter().apply {
            setFormatStrings(
                DatabaseFoodAdapter.DatabaseFoodAdapterFormatStrings(
                    quantity = getString(R.string.row_food_quantity),
                    calories = getString(R.string.row_food_calories),
                    protein = getString(R.string.row_food_protein),
                    carbs = getString(R.string.row_food_carbs),
                    fat = getString(R.string.row_food_fat)
                )
            )
        }
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
