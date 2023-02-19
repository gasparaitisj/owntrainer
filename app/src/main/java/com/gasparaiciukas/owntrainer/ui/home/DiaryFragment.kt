package com.gasparaiciukas.owntrainer.home

import android.os.Build
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.utils.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.fragment.setupBottomNavigation
import com.gasparaiciukas.owntrainer.utils.fragment.setupNavigationView
import com.gasparaiciukas.owntrainer.utils.network.Status
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
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        setupBottomNavigation(
            bottomNavigation = binding.bottomNavigation,
            navController = findNavController(),
            checkedItemId = R.id.diaryFragment
        )
        setupNavigationView(
            navigationView = binding.navigationView,
            drawerLayout = binding.drawerLayout,
            navController = findNavController(),
            checkedItem = R.id.diaryFragment
        )

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout?.open()
        }

        binding.btnAddFood.setOnClickListener {
            findNavController().navigate(
                DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment()
            )
        }

        binding.cardNavigation.btnBack.setOnClickListener {
            viewModel.updateUserToPreviousDay()
        }

        binding.cardNavigation.tvDate.setOnClickListener {
            viewModel.updateUserToCurrentDay()
        }

        binding.cardNavigation.btnForward.setOnClickListener {
            viewModel.updateUserToNextDay()
        }
    }

    private fun setStatistics(uiState: DiaryUiState) {
        // Navigation bar
        binding.cardNavigation.tvDate.text = getString(
            R.string.date_navigation_formatted,
            DateFormatter.dayOfWeekToString(uiState.user.dayOfWeek, requireContext()),
            DateFormatter.monthOfYearToString(uiState.user.month, requireContext()),
            uiState.user.dayOfMonth.toString()
        )

        binding.cardStatistics.tvCaloriesPercentage.apply {
            val caloriesPercentage = uiState.caloriesPercentage.roundToInt()
            text = getString(
                R.string.append_percent_sign,
                caloriesPercentage.toString()
            )
            if (uiState.caloriesPercentage >= 100) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLarge)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLarge)
                }
            }
        }

        binding.cardStatistics.tvProteinPercentage.apply {
            val proteinPercentage = uiState.proteinPercentage.roundToInt()
            text = getString(
                R.string.append_percent_sign,
                proteinPercentage.toString()
            )
            if (uiState.proteinPercentage >= 100) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLarge)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLarge)
                }
            }
        }
        binding.cardStatistics.tvFatPercentage.apply {
            val fatPercentage = uiState.fatPercentage.roundToInt()
            text = getString(
                R.string.append_percent_sign,
                fatPercentage.toString()
            )
            if (uiState.fatPercentage >= 100) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLarge)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLarge)
                }
            }
        }
        binding.cardStatistics.tvCarbsPercentage.apply {
            val carbsPercentage = uiState.carbsPercentage.roundToInt()
            text = getString(
                R.string.append_percent_sign,
                carbsPercentage.toString()
            )
            if (uiState.carbsPercentage >= 100) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLargeBold)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_OwnTrainer_LabelLarge)
                } else {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, R.style.TextAppearance_OwnTrainer_LabelLarge)
                }
            }
        }
        binding.cardStatistics.tvCaloriesCount.text =
            getString(
                R.string.calories_count_formatted,
                uiState.caloriesConsumed.roundToInt().toString(),
                uiState.user.dailyKcalIntake.roundToInt().toString()
            )
        binding.cardStatistics.tvProteinCount.text =
            getString(
                R.string.macros_count_formatted,
                uiState.proteinConsumed.roundToInt().toString(),
                uiState.user.dailyProteinIntakeInG.roundToInt().toString()
            )
        binding.cardStatistics.tvFatCount.text =
            getString(
                R.string.macros_count_formatted,
                uiState.fatConsumed.roundToInt().toString(),
                uiState.user.dailyFatIntakeInG.roundToInt().toString()
            )
        binding.cardStatistics.tvCarbsCount.text =
            getString(
                R.string.macros_count_formatted,
                uiState.carbsConsumed.roundToInt().toString(),
                uiState.user.dailyCarbsIntakeInG.roundToInt().toString()
            )
    }

    private fun initRecyclerView() {
        mealAdapter = MealAdapter().apply {
            setFormatStrings(
                MealAdapter.MealAdapterFormatStrings(
                    calories = getString(R.string.row_meal_calories)
                )
            )
        }
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
