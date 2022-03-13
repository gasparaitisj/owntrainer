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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import kotlin.math.roundToInt

@AndroidEntryPoint
class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MealAdapter

    lateinit var viewModel: DiaryViewModel

    private val singleClickListener: (mealWithFoodEntries: MealWithFoodEntries, position: Int) -> Unit =
        { mealWithFoodEntries: MealWithFoodEntries, _: Int ->
            val action = DiaryFragmentDirections.actionDiaryFragmentToMealItemFragment(
                mealWithFoodEntries.meal.mealId,
                viewModel.diaryEntryWithMeals.diaryEntry.diaryEntryId
            )
            findNavController().navigate(action)
        }

    private val longClickListener: (mealId: Int, position: Int) -> Unit = { mealId, position ->
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteMealFromDiary(
                viewModel.diaryEntryWithMeals.diaryEntry.diaryEntryId,
                mealId
            )
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, adapter.itemCount)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        // Data loading chain:
        // User -> DiaryEntry -> List<MealWithFoodEntries>
        viewModel.ldUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.flUser = MutableStateFlow(user)
                viewModel.user = user
                viewModel.currentDay = LocalDate.of(user.year, user.month, user.dayOfMonth)
                Timber.d("user loaded! ${user.month} ${user.dayOfMonth}")
            } else {
                Timber.d("user is null or not found...")
            }
        }

        viewModel.ldDiaryEntryWithMeals.observe(viewLifecycleOwner) { diaryEntryWithMeals ->
            if (diaryEntryWithMeals != null) {
                Timber.d("ldDiaryEntryWithMeals observe!")
                viewModel.diaryEntryWithMeals = diaryEntryWithMeals
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.calculateData()
                    initUi()
                }
            } else {
                Timber.d("diaryEntryWithMeals is null... creating new entry.")
                viewModel.createDiaryEntry()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
    }

    fun initUi() {
        initNavigation()
        initStatistics()
        binding.scrollView.visibility = View.VISIBLE
    }

    fun initNavigation() {
        binding.cardNavigation.tvDayOfWeek.text =
            DateFormatter.dayOfWeekToString(viewModel.currentDay.dayOfWeek.value)
        binding.cardNavigation.tvMonthOfYear.text =
            DateFormatter.monthOfYearToString(viewModel.currentDay.monthValue)
        binding.cardNavigation.tvDayOfMonth.text = viewModel.currentDay.dayOfMonth.toString()

        // Add meal to diary on FAB clicked
        binding.fab.setOnClickListener {
            val action = DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment(
                viewModel.diaryEntryWithMeals.diaryEntry.diaryEntryId
            )
            findNavController().navigate(action)
        }

        // Navigation (back button)
        binding.cardNavigation.btnBack.setOnClickListener {
            // Refresh fragment and show previous day
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateUserToPreviousDay()
            }
        }

        // Navigation (date layout)
        binding.cardNavigation.layoutDate.setOnClickListener {
            // Refresh fragment and show current day
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateUserToCurrentDay()
            }
        }

        // Navigation (forward button)
        binding.cardNavigation.btnForward.setOnClickListener {
            // Refresh fragment and show next day
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateUserToNextDay()
            }
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

    private fun initStatistics() {
        initRecyclerView()
        // Intakes
        binding.cardStatistics.tvCaloriesIntake.text =
            viewModel.user.dailyKcalIntake.roundToInt().toString()
        binding.cardStatistics.tvProteinIntake.text =
            viewModel.user.dailyProteinIntakeInG.roundToInt().toString()
        binding.cardStatistics.tvFatIntake.text =
            viewModel.user.dailyFatIntakeInG.roundToInt().toString()
        binding.cardStatistics.tvCarbsIntake.text =
            viewModel.user.dailyCarbsIntakeInG.roundToInt().toString()

        // Total calories of day
        binding.cardStatistics.tvCaloriesConsumed.text =
            viewModel.caloriesConsumed.roundToInt().toString()
        binding.cardStatistics.tvProteinConsumed.text =
            viewModel.proteinConsumed.roundToInt().toString()
        binding.cardStatistics.tvFatConsumed.text = viewModel.fatConsumed.roundToInt().toString()
        binding.cardStatistics.tvCarbsConsumed.text =
            viewModel.carbsConsumed.roundToInt().toString()

        // Percentage of daily intake
        binding.cardStatistics.tvCaloriesPercentage.text =
            viewModel.caloriesPercentage.roundToInt().toString()
        binding.cardStatistics.tvProteinPercentage.text =
            viewModel.proteinPercentage.roundToInt().toString()
        binding.cardStatistics.tvFatPercentage.text =
            viewModel.fatPercentage.roundToInt().toString()
        binding.cardStatistics.tvCarbsPercentage.text =
            viewModel.carbsPercentage.roundToInt().toString()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        // val passLambda: (_1: Meal, _2: Int) -> Unit = { _: Meal, _: Int -> }
        adapter = MealAdapter(
            viewModel.mealsWithFoodEntries.toMutableList(),
            singleClickListener,
            longClickListener
        )
        binding.cardMeals.recyclerView.layoutManager = layoutManager
        binding.cardMeals.recyclerView.adapter = adapter
        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
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