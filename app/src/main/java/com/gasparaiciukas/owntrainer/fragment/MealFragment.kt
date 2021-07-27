package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentMealBinding
import com.gasparaiciukas.owntrainer.viewmodel.MealViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import io.realm.Realm

class MealFragment : Fragment() {
    private var _binding: FragmentMealBinding? = null
    private val binding get() = _binding!!

    private lateinit var supportFragmentManager: FragmentManager

    private lateinit var adapter: MealAdapter

    private val listener: (meal: Meal, position: Int) -> Unit = { _: Meal, position: Int ->
        val action = MealFragmentDirections.actionMealFragmentToMealItemFragment(position)
        findNavController().navigate(action)
    }

    private val viewModel: MealViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager = requireActivity().supportFragmentManager
    }

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
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        adapter = MealAdapter(viewModel.meals, listener)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }
}