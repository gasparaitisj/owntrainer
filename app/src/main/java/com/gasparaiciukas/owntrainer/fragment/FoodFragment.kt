package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.NetworkFoodAdapter
import com.gasparaiciukas.owntrainer.databinding.FragmentFoodBinding
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodFragment : Fragment(R.layout.fragment_food) {
    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: NetworkFoodAdapter

    lateinit var viewModel: FoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[FoodViewModel::class.java]
        viewModel.ldFoods.observe(viewLifecycleOwner) {
            it?.also { refreshUi(it) }
        }
        viewModel.ldResponse.observe(viewLifecycleOwner) {
            if (it.status == Status.ERROR) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        setListeners()
        initRecyclerView()
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun refreshUi(foods: List<Food>) {
        reloadRecyclerView(foods)
    }

    private fun reloadRecyclerView(foods: List<Food>) {
        adapter.items = foods
        adapter.setOnClickListeners(
            singleClickListener = { _: Food, position: Int ->
                findNavController().navigate(
                    FoodFragmentDirections.actionFoodFragmentToNetworkFoodItemFragment(
                        position = position,
                        foodItem = foods[position]
                    )
                )
            },
            longClickListener = null
        )
    }

    private fun setListeners() {
        // Send get request on end icon clicked
        binding.layoutEtSearch.setEndIconOnClickListener {
            if (!TextUtils.isEmpty(binding.etSearch.text)) {
                viewModel.getFoods(binding.etSearch.text.toString())
            }
        }

        // Also send get request on keyboard search button clicked
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(binding.etSearch.text)) {
                    viewModel.getFoods(binding.etSearch.text.toString())
                }
                handled = true
            }
            handled
        }

        // Tabs (foods or meals)
        binding.layoutTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {
                    findNavController().navigate(
                        FoodFragmentDirections.actionFoodFragmentToMealFragment()
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.bottomNavigation, findNavController())
        NavigationUI.setupWithNavController(binding.navigationView, findNavController())
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }

    private fun initRecyclerView() {
        adapter = NetworkFoodAdapter()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { binding.bottomNavigation.visibility = View.GONE }
                else { binding.bottomNavigation.visibility = View.VISIBLE }
            }
        })
    }
}