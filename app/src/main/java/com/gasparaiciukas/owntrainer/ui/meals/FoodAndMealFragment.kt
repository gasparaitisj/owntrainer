package com.gasparaiciukas.owntrainer.meals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentFoodAndMealBinding
import com.gasparaiciukas.owntrainer.utils.adapter.FoodAndMealAdapter
import com.gasparaiciukas.owntrainer.utils.fragment.setupBottomNavigation
import com.gasparaiciukas.owntrainer.utils.fragment.setupNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodAndMealFragment : Fragment(R.layout.fragment_food_and_meal) {
    private var _binding: FragmentFoodAndMealBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<FoodAndMealViewModel>()

    private val args by navArgs<FoodAndMealFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodAndMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initNavigation()
    }

    private fun initNavigation() {
        val checkedItem: Int
        when (viewModel.index) {
            0 -> {
                checkedItem = R.id.foodFragment
                binding.topAppBar.title = getString(R.string.foods)
            }
            else -> {
                checkedItem = R.id.mealFragment
                binding.topAppBar.title = getString(R.string.meals)
            }
        }
        setupBottomNavigation(
            bottomNavigation = binding.bottomNavigation,
            navController = findNavController(),
            checkedItemId = R.id.mealFragment
        )
        setupNavigationView(
            viewPager = binding.viewPager,
            navigationView = binding.navigationView,
            drawerLayout = binding.drawerLayout,
            navController = findNavController(),
            checkedItem = checkedItem
        )
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViewPager() {
        binding.viewPager.adapter = FoodAndMealAdapter(childFragmentManager, lifecycle)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (viewModel.isFirstTime) {
                    viewModel.isFirstTime = false
                } else {
                    viewModel.index = position
                }
                when (position) {
                    0 -> {
                        binding.navigationView.setCheckedItem(R.id.foodFragment)
                    }
                    1 -> {
                        binding.navigationView.setCheckedItem(R.id.mealFragment)
                    }
                }
            }
        })

        TabLayoutMediator(binding.layoutTab, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.foods)
                }
                1 -> {
                    tab.text = getString(R.string.meals)
                }
            }
        }.attach()

        binding.viewPager.doOnLayout { viewPager ->
            println("index: ${viewModel.index}")
            (viewPager as ViewPager2).setCurrentItem(viewModel.index, false)
        }
    }
}
