package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.adapter.NetworkFoodAdapter
import com.gasparaiciukas.owntrainer.databinding.FragmentFoodBinding
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class FoodFragment : Fragment() {
    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NetworkFoodAdapter

    private var foods: MutableList<Food> = mutableListOf()

    private val listener: (food: Food, position: Int) -> Unit = { _: Food, position: Int ->
        val action =
            FoodFragmentDirections.actionFoodFragmentToFoodItemFragment(
                position = position,
                foodItem = foods[position]
            )
        findNavController().navigate(action)
    }

    private val viewModel: FoodViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        viewModel.foods.observe(viewLifecycleOwner, Observer { foods ->
            reloadRecyclerView(foods)
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun reloadRecyclerView(foods: List<Food>) {
        val itemCount = adapter.itemCount
        this.foods.clear()
        adapter.notifyItemRangeRemoved(0, itemCount)
        this.foods.addAll(foods)
        adapter.notifyItemRangeInserted(0, this.foods.size)
    }

    private fun initUi() {
        initRecyclerView()
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

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        adapter = NetworkFoodAdapter(foods, listener)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }
}