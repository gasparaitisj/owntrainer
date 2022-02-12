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
import androidx.navigation.fragment.navArgs
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentNetworkFoodItemBinding
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.gasparaiciukas.owntrainer.viewmodel.NetworkFoodItemViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class NetworkFoodItemFragment : Fragment() {
    private var _binding: FragmentNetworkFoodItemBinding? = null
    private val binding get() = _binding!!

    private val args: NetworkFoodItemFragmentArgs by navArgs()

    private val viewModel by viewModels<NetworkFoodItemViewModel>()

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
        viewModel.ldUser.observe(viewLifecycleOwner) {
            if (it != null) {
                Timber.d("data is loaded...")
                viewModel.loadData()
                viewModel.user = it
                initUi()
            } else {
                Timber.d("data is not yet loaded...")
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
        initTextViews()
        initPieChart()
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
            "${viewModel.calories.roundToInt()}\nkCal" // calorie text inside inner circle
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
        binding.tvCarbsWeight.text = viewModel.carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format(
                "%s %%",
                (viewModel.carbs / viewModel.user.dailyCarbsIntakeInG * 100).roundToInt()
            )
        binding.tvFatWeight.text = viewModel.fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format(
                "%s %%",
                (viewModel.fat / viewModel.user.dailyFatIntakeInG * 100).roundToInt()
            )
        binding.tvProteinWeight.text = viewModel.protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format(
                "%s %%",
                (viewModel.protein / viewModel.user.dailyProteinIntakeInG * 100).roundToInt()
            )
        binding.tvCaloriesCount.text = viewModel.calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format(
                "%s %%",
                (viewModel.calories / viewModel.user.dailyKcalIntake * 100).roundToInt()
            )
    }

    private fun initNavigation() {
        binding.topAppBar.title = viewModel.title
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.topAppBar.menu.findItem(R.id.btn_add_to_meal).setOnMenuItemClickListener {
            val action =
                NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToSelectMealItemFragment(
                    args.foodItem,
                    binding.etWeight.text.toString().toInt()
                )
            findNavController().navigate(action)
            true
        }
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