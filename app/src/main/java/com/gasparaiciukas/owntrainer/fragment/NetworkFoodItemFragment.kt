package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.gasparaiciukas.owntrainer.viewmodel.NetworkFoodItemUiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
            pieChart = binding.pieChart,
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
            // Navigate back to FoodFragment manually, because popping back stack will cause
            // all RecyclerView items to recreate again, which is going to cause lag
            findNavController().navigate(
                NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToFoodFragment()
            )
        }

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
        requireActivity().onBackPressedDispatcher.addCallback(callback)
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
                .setTitle(getString(R.string.quantity_no_colon))
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