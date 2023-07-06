//package com.gasparaiciukas.owntrainer.ui.meals.food.fragment
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.gasparaiciukas.owntrainer.R
//import com.gasparaiciukas.owntrainer.databinding.FragmentAddFoodToMealBinding
//import com.gasparaiciukas.owntrainer.utils.adapter.MealAdapter
//import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//@AndroidEntryPoint
//class AddFoodToMealFragment : Fragment(R.layout.fragment_add_food_to_meal) {
//    private var _binding: FragmentAddFoodToMealBinding? = null
//    private val binding get() = _binding!!
//
//    lateinit var mealAdapter: MealAdapter
//
//    lateinit var sharedViewModel: FoodViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddFoodToMealBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        sharedViewModel = ViewModelProvider(requireActivity())[FoodViewModel::class.java]
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                sharedViewModel.meals.collectLatest {
//                    it?.let { refreshUi(it) }
//                }
//            }
//        }
//        initUi()
//    }
//
//    fun initUi() {
//        initNavigation()
//        initRecyclerView()
//    }
//
//    private fun refreshUi(items: List<MealWithFoodEntries>) {
//        mealAdapter.items = items
//        binding.scrollView.visibility = View.VISIBLE
//    }
//
//    private fun initNavigation() {
//        binding.topAppBar.setNavigationOnClickListener {
//            findNavController().popBackStack()
//        }
//    }
//
//    private fun initRecyclerView() {
//        mealAdapter = MealAdapter().apply {
//            setFormatStrings(
//                MealAdapter.MealAdapterFormatStrings(
//                    calories = getString(R.string.row_meal_calories)
//                )
//            )
//        }
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = mealAdapter
//        }
//        mealAdapter.setOnClickListeners(
//            singleClickListener = { mealWithFoodEntries: MealWithFoodEntries, _: Int ->
//                viewLifecycleOwner.lifecycleScope.launch {
//                    sharedViewModel.addFoodToMeal(mealWithFoodEntries)
//                    findNavController().popBackStack()
//                }
//            }
//        )
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
