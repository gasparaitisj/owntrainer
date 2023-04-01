package com.gasparaiciukas.owntrainer.ui.meals.food.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.DialogNetworkFoodItemQuantityBinding
import com.gasparaiciukas.owntrainer.databinding.FragmentNetworkFoodItemBinding
import com.gasparaiciukas.owntrainer.utils.fragment.setPieChart
import com.gasparaiciukas.owntrainer.utils.network.Status
import com.gasparaiciukas.owntrainer.utils.other.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NetworkFoodItemFragment : Fragment(R.layout.fragment_network_food_item) {
    private var _binding: FragmentNetworkFoodItemBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedViewModel: FoodViewModel

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
        sharedViewModel = ViewModelProvider(requireActivity())[FoodViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.networkFoodItemUiState.collectLatest { uiState ->
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
    }

    fun refreshUi(uiState: NetworkFoodItemUiState) {
        setNavigation()
        setTextViews(uiState)
        setPieChart(
            carbsPercentage = uiState.carbsPercentage,
            fatPercentage = uiState.fatPercentage,
            proteinPercentage = uiState.proteinPercentage,
            calories = uiState.calories,
            pieChart = binding.cardNutrition.pieChart,
            context = requireContext()
        )
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun setNavigation() {
        binding.topAppBar.menu.findItem(R.id.btn_add_to_meal).setOnMenuItemClickListener {
            findNavController().navigate(
                NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToAddFoodToMealFragment()
            )
            true
        }
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setTextViews(uiState: NetworkFoodItemUiState) {
        binding.topAppBar.title = uiState.title
        binding.cardNutrition.tvCarbsWeight.text = getString(
            R.string.append_g,
            uiState.carbs.roundToInt().toString()
        )
        binding.cardNutrition.tvCarbsPercentage.text = getString(
            R.string.append_percent_sign,
            (uiState.carbs / uiState.user.dailyCarbsIntakeInG * 100).roundToInt().toString()
        )
        binding.cardNutrition.tvFatWeight.text = getString(
            R.string.append_g,
            uiState.fat.roundToInt().toString()
        )
        binding.cardNutrition.tvFatPercentage.text = getString(
            R.string.append_percent_sign,
            (uiState.fat / uiState.user.dailyFatIntakeInG * 100).roundToInt().toString()
        )
        binding.cardNutrition.tvProteinWeight.text = getString(
            R.string.append_g,
            uiState.protein.roundToInt().toString()
        )
        binding.cardNutrition.tvProteinPercentage.text = getString(
            R.string.append_percent_sign,
            (uiState.protein / uiState.user.dailyProteinIntakeInG * 100).roundToInt().toString()
        )
        binding.cardNutrition.tvCaloriesWeight.text = getString(
            R.string.append_kcal,
            uiState.calories.roundToInt().toString()
        )
        binding.cardNutrition.tvCaloriesPercentage.text = getString(
            R.string.append_percent_sign,
            (uiState.calories / uiState.user.dailyKcalIntake * 100).roundToInt().toString()
        )
        binding.etQuantity.setText(sharedViewModel.quantity.toString())
        binding.etQuantity.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_network_food_item_quantity, null)
            val dialogBinding = DialogNetworkFoodItemQuantityBinding.bind(view)
            var quantity = ""
            dialogBinding.dialogEtQuantity.doOnTextChanged { text, _, _, _ ->
                quantity = text.toString()
                val validation = isQuantityCorrect(quantity)
                if (validation == null) {
                    dialogBinding.dialogLayoutEtQuantity.error = null
                } else {
                    dialogBinding.dialogLayoutEtQuantity.error = validation
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(getString(R.string.quantity))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    if (isQuantityCorrect(quantity) == null) {
                        sharedViewModel.quantity = quantity.toInt()
                        binding.etQuantity.setText(sharedViewModel.quantity.toString())
                    }
                }
                .show()
        }
    }

    private fun isQuantityCorrect(s: String): String? {
        val quantity: Int
        try {
            quantity = s.toInt()
        } catch (e: NumberFormatException) {
            return getString(R.string.number_must_be_valid)
        }
        if ((quantity in Constants.QUANTITY_MINIMUM..Constants.QUANTITY_MAXIMUM).not()) {
            return getString(R.string.quantity_must_be_valid)
        }
        return null
    }
}
