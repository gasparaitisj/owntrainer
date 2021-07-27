package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter
import com.gasparaiciukas.owntrainer.databinding.FragmentFoodBinding
import com.gasparaiciukas.owntrainer.network.FoodApi
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import timber.log.Timber

class FoodFragment : Fragment() {
    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FoodApiAdapter

    private lateinit var supportFragmentManager: FragmentManager

    private var foodsApi: MutableList<FoodApi> = mutableListOf()

    private val listener: (food: FoodApi, position: Int) -> Unit = { _: FoodApi, position: Int ->
        val action =
            FoodFragmentDirections.actionFoodFragmentToFoodItemFragment(
                position = position,
                foodItem = foodsApi[position]
            )
        findNavController().navigate(action)
    }

    private val viewModel: FoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager = requireActivity().supportFragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        viewModel.foodsApi.observe(viewLifecycleOwner, Observer { foods ->
            foodsApi.apply {
                foodsApi.clear()
                foodsApi.addAll(foods)
            }
            adapter.apply {
                notifyItemRangeRemoved(0, adapter.itemCount)
                notifyItemRangeInserted(0, foodsApi.size)
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        // Set up recycler view
        val layoutManager = LinearLayoutManager(context)
        adapter = FoodApiAdapter(foodsApi, listener)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    private fun initUi() {
        // Send get request on end icon clicked
        binding.layoutEtSearch.setEndIconOnClickListener {
            if (!TextUtils.isEmpty(binding.etSearch.text)) {
                viewModel.sendGet(binding.etSearch.text.toString())
            }
        }

        // Also send get request on keyboard search button clicked
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(binding.etSearch.text)) {
                    viewModel.sendGet(binding.etSearch.text.toString())
                }
                handled = true
            }
            handled
        }

        // Tabs (foods or meals)
        binding.layoutTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {
                    val action = FoodFragmentDirections.actionFoodFragmentToMealFragment()
                    findNavController().navigate(action)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}