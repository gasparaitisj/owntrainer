package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentSelectMealItemBinding
import com.gasparaiciukas.owntrainer.viewmodel.BundleViewModelFactory
import com.gasparaiciukas.owntrainer.viewmodel.SelectMealItemViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectMealItemFragment : Fragment() {
    private var _binding: FragmentSelectMealItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MealAdapter

    private val args: SelectMealItemFragmentArgs by navArgs()

    private lateinit var viewModel: SelectMealItemViewModel
    private lateinit var viewModelFactory: BundleViewModelFactory

    private val listener: (meal: Meal, position: Int) -> Unit = { meal: Meal, _: Int ->
        viewModel.addFoodToMeal(meal)
        findNavController().popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectMealItemBinding.inflate(inflater, container, false)

        val bundle = Bundle().apply {
            putParcelable("foodItem", args.foodItem)
            putInt("quantity", args.quantity)
        }
        viewModelFactory = BundleViewModelFactory(bundle)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SelectMealItemViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initRecyclerView()
    }

    private fun initUi() {
        initNavigation()
        initRecyclerView()
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView() {
        val passLambda: (_: Int) -> Unit = { _: Int -> }
        adapter = MealAdapter(viewModel.meals, listener, passLambda)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideBottomNavigationUp()
        _binding = null
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