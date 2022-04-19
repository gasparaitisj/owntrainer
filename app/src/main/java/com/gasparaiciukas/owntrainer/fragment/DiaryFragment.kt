package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.viewmodel.DiaryUiState
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DiaryFragment : Fragment(R.layout.fragment_diary) {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    lateinit var mealAdapter: MealAdapter

    lateinit var viewModel: DiaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DiaryViewModel::class.java]

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

    fun initUi() {
        initNavigation()
        initRecyclerView()
    }

    private fun refreshUi(uiState: DiaryUiState) {
        setStatistics(uiState)
        setRecyclerView(uiState)
        binding.scrollView.visibility = View.VISIBLE
    }

    fun initNavigation() {
        setupWithNavController(binding.bottomNavigation, findNavController())
        setupWithNavController(binding.navigationView, findNavController())

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(
                DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment()
            )
        }

        binding.cardNavigation.btnBack.setOnClickListener {
            viewModel.updateUserToPreviousDay()
        }

        binding.cardNavigation.layoutDate.setOnClickListener {
            viewModel.updateUserToCurrentDay()
        }

        binding.cardNavigation.btnForward.setOnClickListener {
            viewModel.updateUserToNextDay()
        }
    }

    private fun setStatistics(uiState: DiaryUiState) {
        // Navigation bar
        binding.cardNavigation.tvDayOfWeek.text =
            DateFormatter.dayOfWeekToString(uiState.user.dayOfWeek)
        binding.cardNavigation.tvMonthOfYear.text =
            DateFormatter.monthOfYearToString(uiState.user.month)
        binding.cardNavigation.tvDayOfMonth.text = uiState.user.dayOfMonth.toString()

        // Intakes
        binding.cardStatistics.tvCaloriesIntake.text =
            uiState.user.dailyKcalIntake.roundToInt().toString()
        binding.cardStatistics.tvProteinIntake.text =
            uiState.user.dailyProteinIntakeInG.roundToInt().toString()
        binding.cardStatistics.tvFatIntake.text =
            uiState.user.dailyFatIntakeInG.roundToInt().toString()
        binding.cardStatistics.tvCarbsIntake.text =
            uiState.user.dailyCarbsIntakeInG.roundToInt().toString()

        // Total calories of day
        binding.cardStatistics.tvCaloriesConsumed.text =
            uiState.caloriesConsumed.roundToInt().toString()
        binding.cardStatistics.tvProteinConsumed.text =
            uiState.proteinConsumed.roundToInt().toString()
        binding.cardStatistics.tvFatConsumed.text = uiState.fatConsumed.roundToInt().toString()
        binding.cardStatistics.tvCarbsConsumed.text =
            uiState.carbsConsumed.roundToInt().toString()

        // Percentage of daily intake
        binding.cardStatistics.tvCaloriesPercentage.text =
            uiState.caloriesPercentage.roundToInt().toString()
        binding.cardStatistics.tvProteinPercentage.text =
            uiState.proteinPercentage.roundToInt().toString()
        binding.cardStatistics.tvFatPercentage.text =
            uiState.fatPercentage.roundToInt().toString()
        binding.cardStatistics.tvCarbsPercentage.text =
            uiState.carbsPercentage.roundToInt().toString()
    }

    private fun initRecyclerView() {
        mealAdapter = MealAdapter()
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mealAdapter
        }
    }

    private fun setRecyclerView(uiState: DiaryUiState) {
        mealAdapter.setOnClickListeners(
            singleClickListener = { mealWithFoodEntries: MealWithFoodEntries, _: Int ->
                findNavController().navigate(
                    DiaryFragmentDirections.actionDiaryFragmentToMealItemFragment(
                        mealId = mealWithFoodEntries.meal.mealId,
                        diaryEntryId = uiState.diaryEntryWithMeals.diaryEntry.diaryEntryId
                    )
                )
            },
            longClickListener = { mealId, _ ->
                viewModel.deleteMealFromDiary(
                    uiState.diaryEntryWithMeals.diaryEntry.diaryEntryId,
                    mealId
                )
            }
        )
        mealAdapter.items = uiState.meals
    }
}