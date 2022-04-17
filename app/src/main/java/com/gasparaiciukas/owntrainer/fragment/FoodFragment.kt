package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.NetworkFoodAdapter
import com.gasparaiciukas.owntrainer.databinding.FragmentFoodBinding
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodFragment : Fragment(R.layout.fragment_food) {
    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    lateinit var networkFoodAdapter: NetworkFoodAdapter

    lateinit var viewModel: FoodViewModel

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[FoodViewModel::class.java]
        viewModel.ldResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = false
                    response.data?.foods?.let { refreshUi(it.toList()) }
                    val totalPages: Int? =
                        (response.data?.totalHits?.div(Constants.Api.QUERY_PAGE_SIZE)?.plus(2))
                    isLastPage = viewModel.pageNumber == totalPages
                    response?.data?.foods?.size?.let { size ->
                        if (size == 0) {
                            if (isLastPage && viewModel.pageNumber != 2) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.could_not_find_any_more_matching_foods),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.could_not_find_any_matching_foods),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = false
                    response.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                    isLoading = true
                }
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
        networkFoodAdapter.items = foods
        networkFoodAdapter.setOnClickListeners(
            singleClickListener = { _: Food, position: Int ->
                viewModel.foodItem = foods[position]
                findNavController().navigate(
                    FoodFragmentDirections.actionFoodFragmentToNetworkFoodItemFragment()
                )
            }
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

        binding.etSearch.doOnTextChanged { _, _, _, _ ->
            viewModel.onQueryChanged()
            if (networkFoodAdapter.items.isNotEmpty()) {
                reloadRecyclerView(listOf())
            }
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
        findNavController().addOnDestinationChangedListener { _, _, _ ->
            viewModel.clearFoods()
        }
        NavigationUI.setupWithNavController(binding.bottomNavigation, findNavController())
        NavigationUI.setupWithNavController(binding.navigationView, findNavController())
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }

    private fun initRecyclerView() {
        networkFoodAdapter = NetworkFoodAdapter()
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = networkFoodAdapter
            addOnScrollListener(onScrollListener)
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.Api.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getFoods(binding.etSearch.text.toString())
                isScrolling = false
            } else {
                recyclerView.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}