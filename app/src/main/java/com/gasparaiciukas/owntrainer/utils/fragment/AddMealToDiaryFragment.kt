package com.gasparaiciukas.owntrainer.utils.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentAddMealToDiaryBinding
import com.gasparaiciukas.owntrainer.utils.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.utils.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.utils.viewmodel.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AddMealToDiaryFragment : Fragment(R.layout.fragment_add_meal_to_diary) {
    private var _binding: FragmentAddMealToDiaryBinding? = null
    private val binding get() = _binding!!

    lateinit var mealAdapter: MealAdapter

    lateinit var sharedViewModel: DiaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMealToDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[DiaryViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.allMeals.collect { meals ->
                    meals?.let { refreshUi(it) }
                }
            }
        }
        initUi()
    }

    fun initUi() {
        initNavigation()
        initRecyclerView()
        binding.scrollView.visibility = View.VISIBLE
    }

    fun refreshUi(items: List<MealWithFoodEntries>) {
        mealAdapter.items = items
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(
                AddMealToDiaryFragmentDirections.actionAddMealToDiaryFragmentToDiaryFragment()
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
        mealAdapter.setOnClickListeners(
            singleClickListener = { mealWithFoodEntries: MealWithFoodEntries, _: Int ->
                sharedViewModel.addMealToDiary(mealWithFoodEntries)
                findNavController().navigate(
                    AddMealToDiaryFragmentDirections.actionAddMealToDiaryFragmentToDiaryFragment()
                )
            }
        )
        binding.recyclerView.apply {
            adapter = mealAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
