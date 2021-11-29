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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.databinding.FragmentMealItemBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.gasparaiciukas.owntrainer.viewmodel.BundleViewModelFactory
import com.gasparaiciukas.owntrainer.viewmodel.MealItemViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import kotlin.math.roundToInt

class MealItemFragment : Fragment() {
    private var _binding: FragmentMealItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DatabaseFoodAdapter

    private val args: MealItemFragmentArgs by navArgs()

    private lateinit var viewModel: MealItemViewModel
    private lateinit var viewModelFactory: BundleViewModelFactory

    private val singleClickListener: (food: FoodEntryParcelable) -> Unit = { food: FoodEntryParcelable ->
        val action = MealItemFragmentDirections.actionMealItemFragmentToDatabaseFoodItemFragment(food)
        findNavController().navigate(action)
    }

    private val longClickListener: (food: FoodEntry, position: Int) -> Unit = { food: FoodEntry, position: Int ->
        viewModel.deleteFoodFromMeal(food, args.position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, (adapter.itemCount - position))
        initTextViews()
        initPieChart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealItemBinding.inflate(inflater, container, false)

        val bundle = Bundle().apply {
            putInt("position", args.position)
            putString("primaryKey", args.primaryKey)
        }
        viewModelFactory = BundleViewModelFactory(bundle)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MealItemViewModel::class.java)

        initUi()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
    }

    private fun initUi() {
        initRecyclerView()
        initTextViews()
        initPieChart()
    }

    private fun initTextViews() {
        binding.tvTitle.text = viewModel.meals[args.position].title
        binding.tvInstructions.text = viewModel.meals[args.position].instructions
        binding.tvInstructions.movementMethod = ScrollingMovementMethod()
        binding.tvCarbsWeight.text = viewModel.carbs.roundToInt().toString()
        binding.tvCarbsPercentage.text =
            String.format("%s %%", (viewModel.carbs / viewModel.carbsDailyIntake * 100).roundToInt())
        binding.tvFatWeight.text = viewModel.fat.roundToInt().toString()
        binding.tvFatPercentage.text =
            String.format("%s %%", (viewModel.fat / viewModel.fatDailyIntake * 100).roundToInt())
        binding.tvProteinWeight.text = viewModel.protein.roundToInt().toString()
        binding.tvProteinPercentage.text =
            String.format("%s %%", (viewModel.protein / viewModel.proteinDailyIntake * 100).roundToInt())
        binding.tvCaloriesCount.text = viewModel.calories.roundToInt().toString()
        binding.tvCaloriesPercentage.text =
            String.format("%s %%", (viewModel.calories / viewModel.calorieDailyIntake * 100).roundToInt())
    }

    private fun initRecyclerView() {
        adapter = DatabaseFoodAdapter(viewModel.foodList, singleClickListener, longClickListener)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
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
        binding.pieChart.centerText = "${viewModel.calories.roundToInt()}\nkCal" // calorie text inside inner circle
        binding.pieChart.setCenterTextSize(14f)
        binding.pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
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