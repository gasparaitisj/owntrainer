package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.FragmentAddFoodToMealBinding
import com.gasparaiciukas.owntrainer.viewmodel.AddFoodToMealViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFoodToMealFragment : Fragment(R.layout.fragment_add_food_to_meal) {
    private var _binding: FragmentAddFoodToMealBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: MealAdapter

    lateinit var viewModel: AddFoodToMealViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFoodToMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AddFoodToMealViewModel::class.java]
        viewModel.ldMeals.observe(viewLifecycleOwner) {
            it?.also { refreshUi(it) }
        }
        initUi()
    }

    fun initUi() {
        initNavigation()
        initRecyclerView()
    }

    fun refreshUi(items: List<MealWithFoodEntries>) {
        adapter.items = items
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView() {
        adapter = MealAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        adapter.setOnClickListeners(
            singleClickListener = { mealWithFoodEntries: MealWithFoodEntries, _: Int ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.addFoodToMeal(mealWithFoodEntries)
                    findNavController().popBackStack()
                }
            },
            longClickListener = null
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}