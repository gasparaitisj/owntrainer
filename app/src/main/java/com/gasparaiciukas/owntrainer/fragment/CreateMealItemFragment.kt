package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentCreateMealItemBinding
import com.gasparaiciukas.owntrainer.viewmodel.CreateMealItemViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMealItemFragment : Fragment(R.layout.fragment_create_meal_item) {
    private var _binding: FragmentCreateMealItemBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: CreateMealItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMealItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateMealItemViewModel::class.java]
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initUi() {
        initNavigation()
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.topAppBar.menu.findItem(R.id.btn_save).setOnMenuItemClickListener {
            viewModel.createMeal(
                binding.etTitle.text.toString(),
                binding.etInstructions.text.toString()
            )
            findNavController().popBackStack()
        }
    }
}