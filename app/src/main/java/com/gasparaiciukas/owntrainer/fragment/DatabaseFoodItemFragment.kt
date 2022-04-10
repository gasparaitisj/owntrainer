package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentDatabaseFoodItemBinding
import com.gasparaiciukas.owntrainer.viewmodel.DatabaseFoodItemUiState
import com.gasparaiciukas.owntrainer.viewmodel.DatabaseFoodItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class DatabaseFoodItemFragment : Fragment(R.layout.fragment_database_food_item) {
    private var _binding: FragmentDatabaseFoodItemBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: DatabaseFoodItemViewModel

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
        viewModel = ViewModelProvider(this)[DatabaseFoodItemViewModel::class.java]
        viewModel.ldUser.observe(viewLifecycleOwner) {
            viewModel.loadData()
        }
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            refreshUi(uiState)
        }
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
    }

    private fun refreshUi(uiState: DatabaseFoodItemUiState) {
        setTextViews(uiState)
        setPieChart(
            carbsPercentage = uiState.carbsPercentage,
            fatPercentage = uiState.fatPercentage,
            proteinPercentage = uiState.proteinPercentage,
            calories = uiState.food.calories.toFloat(),
            pieChart = binding.pieChart,
            context = requireContext()
        )
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setTextViews(uiState: DatabaseFoodItemUiState) {
        binding.topAppBar.title = uiState.food.title
        binding.tvQuantityCount.text = uiState.food.quantityInG.toString()
        binding.tvCarbsWeight.text = uiState.food.carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format(
                "%s %%",
                uiState.carbsDailyIntakePercentage.roundToInt()
            )
        binding.tvFatWeight.text = uiState.food.fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format(
                "%s %%",
                uiState.fatDailyIntakePercentage.roundToInt()
            )
        binding.tvProteinWeight.text = uiState.food.protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format(
                "%s %%",
                uiState.proteinDailyIntakePercentage.roundToInt()
            )
        binding.tvCaloriesCount.text = uiState.food.calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format(
                "%s %%",
                uiState.caloriesDailyIntakePercentage.roundToInt()
            )
    }
}