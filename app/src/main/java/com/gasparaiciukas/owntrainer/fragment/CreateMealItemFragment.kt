package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.databinding.FragmentCreateMealItemBinding
import com.gasparaiciukas.owntrainer.viewmodel.CreateMealItemViewModel

class CreateMealItemFragment : Fragment() {
    private var _binding: FragmentCreateMealItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateMealItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMealItemBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        binding.btnSave.setOnClickListener {
            viewModel.addMealToDatabase(
                binding.etTitle.text.toString(),
                binding.etInstructions.text.toString()
            )
            findNavController().popBackStack()
        }
    }
}