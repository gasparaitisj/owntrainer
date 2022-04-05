package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.viewmodel.DiaryUiState
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DiaryFragment : Fragment(R.layout.fragment_diary) {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: MealAdapter

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

        viewModel.ldDiaryEntryWithMeals.observe(viewLifecycleOwner) { diaryEntryWithMeals ->
            if (diaryEntryWithMeals != null) {
                viewModel.calculateData()
            } else {
                viewModel.createDiaryEntry()
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            refreshUi(uiState)
        }
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
    }

    fun initUi() {
        initNavigation()
        initRecyclerView()
    }

    fun refreshUi(uiState: DiaryUiState) {
        setStatistics(uiState)
        setRecyclerView(uiState)
        binding.scrollView.visibility = View.VISIBLE
    }

    fun initNavigation() {
        // Add meal to diary on FAB clicked
        binding.fab.setOnClickListener {
            findNavController().navigate(
                DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment()
            )
        }

        // Navigation (back button)
        binding.cardNavigation.btnBack.setOnClickListener {
            // Refresh fragment and show previous day
            viewModel.updateUserToPreviousDay()
        }

        // Navigation (date layout)
        binding.cardNavigation.layoutDate.setOnClickListener {
            // Refresh fragment and show current day
            viewModel.updateUserToCurrentDay()
        }

        // Navigation (forward button)
        binding.cardNavigation.btnForward.setOnClickListener {
            // Refresh fragment and show next day
            viewModel.updateUserToNextDay()
        }

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
        binding.navigationView.setCheckedItem(R.id.home)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.home -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.foods -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentToFoodFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.meals -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentToMealFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.progress -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                DiaryFragmentDirections.actionDiaryFragmentToProgressFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.profile -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                DiaryFragmentDirections.actionDiaryFragmentToProfileFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.settings -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                DiaryFragmentDirections.actionDiaryFragmentToSettingsFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
            }
            true
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
        adapter = MealAdapter()
        binding.cardMeals.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.cardMeals.recyclerView.adapter = adapter
        binding.scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                // Scroll up
                if (scrollY < oldScrollY) {
                    slideBottomNavigationUp()
                    binding.fab.show()
                }
                // Scroll down
                if (scrollY > oldScrollY) {
                    slideBottomNavigationDown()
                    binding.fab.hide()
                }
        })
    }

    private fun setRecyclerView(uiState: DiaryUiState) {
        adapter.setOnClickListeners(
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
        adapter.items = uiState.meals
    }

    fun slideBottomNavigationUp() {
        val botNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val layoutParams = botNav?.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = layoutParams.behavior
            if (behavior is HideBottomViewOnScrollBehavior) {
                behavior.slideUp(botNav)
            }
        }
    }

    fun slideBottomNavigationDown() {
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