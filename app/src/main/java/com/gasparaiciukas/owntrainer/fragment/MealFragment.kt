package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.FragmentMealBinding
import com.gasparaiciukas.owntrainer.viewmodel.MealViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealFragment : Fragment(R.layout.fragment_meal) {
    private var _binding: FragmentMealBinding? = null
    private val binding get() = _binding!!

    lateinit var mealAdapter: MealAdapter

    lateinit var viewModel: MealViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MealViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meals.collectLatest { meals ->
                    meals?.let { refreshUi(it) }
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
        setListeners()
        initRecyclerView()
    }

    private fun refreshUi(items: List<MealWithFoodEntries>) {
        mealAdapter.items = items
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun setListeners() {
        // Set up FAB
        binding.fab.setOnClickListener {
            findNavController().navigate(
                MealFragmentDirections.actionMealFragmentToCreateMealItemFragment()
            )
        }
    }

    private fun initRecyclerView() {
        mealAdapter = MealAdapter().apply {
            setFormatStrings(
                MealAdapter.MealAdapterFormatStrings(
                    calories = getString(R.string.row_meal_calories)
                )
            )
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mealAdapter
        }
        mealAdapter.setOnClickListeners(
            singleClickListener = { mealWithFoodEntries: MealWithFoodEntries, _: Int ->
                findNavController().navigate(
                    MealFragmentDirections.actionMealFragmentToMealItemFragment(
                        mealId = mealWithFoodEntries.meal.mealId,
                        diaryEntryId = -1
                    )
                )
            },
            longClickListener = { mealId, _ ->
                viewModel.deleteMeal(mealId)
                Snackbar.make(
                    binding.coordinatorLayout,
                    R.string.snackbar_meal_deleted,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        )
    }
}