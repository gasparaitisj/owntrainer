package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber
import kotlin.math.roundToInt

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MealAdapter

    private val viewModel: DiaryViewModel by viewModels()

    private val singleClickListener: (meal: Meal, position: Int) -> Unit = { _: Meal, position: Int ->
        val action = DiaryFragmentDirections.actionDiaryFragmentToMealItemFragment(position, viewModel.diaryEntry.yearAndDayOfYear)
        findNavController().navigate(action)
    }

    private val longClickListener: (position: Int) -> Unit = { position: Int ->
        Timber.d("Position: $position")
        viewModel.deleteMealFromDiary(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, (adapter.itemCount - position))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        viewModel.dataChanged.observe(viewLifecycleOwner, Observer {
            setTextViews()
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
    }

    private fun initUi() {
        setTextViews()
        setListeners()
        initNavigation()
        initRecyclerView()
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
        binding.navigationView.setCheckedItem(R.id.home)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.home -> {
                    binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.foods -> {
                    binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentToFoodFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.meals -> {
                    binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentToMealFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.progress -> {
                    binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentToProgressFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.profile -> {
                    binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentToProfileFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.settings -> {
                    binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = DiaryFragmentDirections.actionDiaryFragmentToSettingsFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
            }
            true
        }
    }

    private fun setTextViews() {
        // Navigation
        binding.cardNavigation.tvDayOfWeek.text = DateFormatter.dayOfWeekToString(viewModel.diaryEntry.dayOfWeek)
        binding.cardNavigation.tvMonthOfYear.text = DateFormatter.monthOfYearToString(viewModel.diaryEntry.monthOfYear)
        binding.cardNavigation.tvDayOfMonth.text = viewModel.diaryEntry.dayOfMonth.toString()

        // Intakes
        binding.cardStatistics.tvCaloriesIntake.text = viewModel.user.dailyKcalIntake.roundToInt().toString()
        binding.cardStatistics.tvProteinIntake.text = viewModel.user.dailyProteinIntakeInG.roundToInt().toString()
        binding.cardStatistics.tvFatIntake.text = viewModel.user.dailyFatIntakeInG.roundToInt().toString()
        binding.cardStatistics.tvCarbsIntake.text = viewModel.user.dailyCarbsIntakeInG.roundToInt().toString()

        // Total calories of day
        binding.cardStatistics.tvCaloriesConsumed.text = viewModel.caloriesConsumed.roundToInt().toString()
        binding.cardStatistics.tvProteinConsumed.text = viewModel.proteinConsumed.roundToInt().toString()
        binding.cardStatistics.tvFatConsumed.text = viewModel.fatConsumed.roundToInt().toString()
        binding.cardStatistics.tvCarbsConsumed.text = viewModel.carbsConsumed.roundToInt().toString()

        // Percentage of daily intake
        binding.cardStatistics.tvCaloriesPercentage.text = viewModel.caloriesPercentage.roundToInt().toString()
        binding.cardStatistics.tvProteinPercentage.text = viewModel.proteinPercentage.roundToInt().toString()
        binding.cardStatistics.tvFatPercentage.text = viewModel.fatPercentage.roundToInt().toString()
        binding.cardStatistics.tvCarbsPercentage.text = viewModel.carbsPercentage.roundToInt().toString()
    }

    private fun setListeners() {
        // Add meal to diary on FAB clicked
        binding.fab.setOnClickListener {
            val action = DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment(
                viewModel.diaryEntry.yearAndDayOfYear
            )
            findNavController().navigate(action)
        }

        // Navigation (back button)
        binding.cardNavigation.btnBack.setOnClickListener {
            // Refresh fragment and show previous day
            viewModel.updateUserToPreviousDay()
            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
            findNavController().navigate(action)
        }

        // Navigation (date layout)
        binding.cardNavigation.layoutDate.setOnClickListener {
            // Refresh fragment and show current day
            viewModel.updateUserToCurrentDay()
            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
            findNavController().navigate(action)
        }

        // Navigation (forward button)
        binding.cardNavigation.btnForward.setOnClickListener {
            // Refresh fragment and show next day
            viewModel.updateUserToNextDay()
            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
            findNavController().navigate(action)
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        val passLambda: (_1: Meal, _2: Int) -> Unit = { _: Meal, _: Int -> }
        adapter = MealAdapter(viewModel.diaryEntry.meals, singleClickListener, longClickListener)
        binding.cardMeals.recyclerView.layoutManager = layoutManager
        binding.cardMeals.recyclerView.adapter = adapter
        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
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