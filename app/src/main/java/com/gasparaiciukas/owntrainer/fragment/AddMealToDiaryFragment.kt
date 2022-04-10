package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.FragmentAddMealToDiaryBinding
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
        initUi()
    }

    fun initUi() {
        initNavigation()
        initRecyclerView()
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(
                AddMealToDiaryFragmentDirections.actionAddMealToDiaryFragmentToDiaryFragment()
            )
        }
    }

    private fun initRecyclerView() {
        mealAdapter = MealAdapter()
        mealAdapter.items = sharedViewModel.ldAllMeals.value ?: listOf()
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