package com.gasparaiciukas.owntrainer.ui.meals.food.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentDatabaseFoodItemBinding
import com.gasparaiciukas.owntrainer.utils.fragment.setPieChart
import com.gasparaiciukas.owntrainer.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    when (uiState.status) {
                        Status.SUCCESS -> {
                            uiState.data?.let { refreshUi(it) }
                        }
                        Status.ERROR -> {
                            if (uiState.messageRes != null) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(uiState.messageRes),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    uiState.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {}
                    }
                    uiState.data?.let { refreshUi(it) }
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
    }

    private fun refreshUi(uiState: DatabaseFoodItemUiState) {
        setTextViews(uiState)
        setPieChart(
            carbsPercentage = uiState.carbsPercentage,
            fatPercentage = uiState.fatPercentage,
            proteinPercentage = uiState.proteinPercentage,
            calories = uiState.food.calories.toFloat(),
            pieChart = binding.cardNutrition.pieChart,
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
        binding.topAppBar.title = getString(
            R.string.database_food_item_fragment_top_app_bar_title,
            uiState.food.title,
            uiState.food.quantityInG.toString()
        )
        binding.cardNutrition.tvCarbsWeight.text = getString(
            R.string.append_g,
            uiState.food.carbs.roundToInt().toString()
        )
        binding.cardNutrition.tvCarbsPercentage.text = getString(
            R.string.append_percent_sign,
            uiState.carbsDailyIntakePercentage.roundToInt().toString()
        )
        binding.cardNutrition.tvFatWeight.text = getString(
            R.string.append_g,
            uiState.food.fat.roundToInt().toString()
        )
        binding.cardNutrition.tvFatPercentage.text = getString(
            R.string.append_percent_sign,
            uiState.fatDailyIntakePercentage.roundToInt().toString()
        )
        binding.cardNutrition.tvProteinWeight.text = getString(
            R.string.append_g,
            uiState.food.protein.roundToInt().toString()
        )
        binding.cardNutrition.tvProteinPercentage.text = getString(
            R.string.append_percent_sign,
            uiState.proteinDailyIntakePercentage.roundToInt().toString()
        )
        binding.cardNutrition.tvCaloriesWeight.text = getString(
            R.string.append_kcal,
            uiState.food.calories.roundToInt().toString()
        )
        binding.cardNutrition.tvCaloriesPercentage.text = getString(
            R.string.append_percent_sign,
            uiState.caloriesDailyIntakePercentage.roundToInt().toString()
        )
    }
}
