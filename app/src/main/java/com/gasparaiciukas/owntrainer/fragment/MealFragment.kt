package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentMealBinding
import com.gasparaiciukas.owntrainer.viewmodel.MealViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import timber.log.Timber

class MealFragment : Fragment() {
    private var _binding: FragmentMealBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MealAdapter

    private val singleClickListener: (meal: Meal, position: Int) -> Unit = { _: Meal, position: Int ->
        val action = MealFragmentDirections.actionMealFragmentToMealItemFragment(position, "")
        findNavController().navigate(action)
    }

    private val longClickListener: (position: Int) -> Unit = { position: Int ->
        viewModel.deleteMealFromMeals(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, (adapter.itemCount - position))
    }

    private val viewModel: MealViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMealBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        // Set up FAB
        binding.fab.setOnClickListener {
            val action = MealFragmentDirections.actionMealFragmentToCreateMealItemFragment()
            findNavController().navigate(action)
        }

        // Tabs (foods or meals)
        binding.layoutTab.getTabAt(1)?.select() // select current tab
        binding.layoutTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    val action = MealFragmentDirections.actionMealFragmentToFoodFragment()
                    findNavController().navigate(action)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // do nothing
            }
        })

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // Scroll down
            if (scrollY > oldScrollY) {
                slideBottomNavigationDown()
                binding.fab.hide()
            }
            // Scroll up
            if (scrollY < oldScrollY) {
                slideBottomNavigationUp()
                binding.fab.show()
            }
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        adapter = MealAdapter(viewModel.meals, singleClickListener, longClickListener)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
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