package com.gasparaiciukas.owntrainer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentNetworkFoodItemBinding
import com.gasparaiciukas.owntrainer.viewmodel.NetworkFoodItemUiState
import com.gasparaiciukas.owntrainer.viewmodel.NetworkFoodItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class NetworkFoodItemFragment : Fragment(R.layout.fragment_network_food_item) {
    private var _binding: FragmentNetworkFoodItemBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NetworkFoodItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNetworkFoodItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NetworkFoodItemViewModel::class.java]
        viewModel.ldUser.observe(viewLifecycleOwner) {
            viewModel.loadData()
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
    }

    private fun refreshUi(uiState: NetworkFoodItemUiState) {
        setTextViews(uiState)
        setPieChart(
            carbsPercentage = uiState.carbsPercentage,
            fatPercentage = uiState.fatPercentage,
            proteinPercentage = uiState.proteinPercentage,
            calories = uiState.calories,
            pieChart = binding.pieChart,
            context = requireContext()
        )
        setNavigation(uiState)
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            // Navigate back to FoodFragment manually, because popping back stack will cause
            // all RecyclerView items to recreate again, which is going to cause lag
            findNavController().navigate(
                NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToFoodFragment()
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Navigate back to FoodFragment manually, because popping back stack will cause
                    // all RecyclerView items to recreate again, which is going to cause lag
                    findNavController().navigate(
                        NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToFoodFragment()
                    )
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private fun setTextViews(uiState: NetworkFoodItemUiState) {
        binding.topAppBar.title = uiState.title
        binding.tvCarbsWeight.text = uiState.carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format(
                "%s %%",
                (uiState.carbs / uiState.user.dailyCarbsIntakeInG * 100).roundToInt()
            )
        binding.tvFatWeight.text = uiState.fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format(
                "%s %%",
                (uiState.fat / uiState.user.dailyFatIntakeInG * 100).roundToInt()
            )
        binding.tvProteinWeight.text = uiState.protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format(
                "%s %%",
                (uiState.protein / uiState.user.dailyProteinIntakeInG * 100).roundToInt()
            )
        binding.tvCaloriesCount.text = uiState.calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format(
                "%s %%",
                (uiState.calories / uiState.user.dailyKcalIntake * 100).roundToInt()
            )
    }

    private fun setNavigation(uiState: NetworkFoodItemUiState) {
        binding.topAppBar.menu.findItem(R.id.btn_add_to_meal).setOnMenuItemClickListener {
            findNavController().navigate(
                NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToAddFoodToMealFragment(
                    uiState.foodItem,
                    binding.etWeight.text.toString().toInt()
                )
            )
            true
        }
    }
}