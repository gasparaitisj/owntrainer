package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.DialogCreateMealItemInstructionsBinding
import com.gasparaiciukas.owntrainer.databinding.DialogCreateMealItemTitleBinding
import com.gasparaiciukas.owntrainer.databinding.FragmentCreateMealItemBinding
import com.gasparaiciukas.owntrainer.viewmodel.MealViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMealItemFragment : Fragment(R.layout.fragment_create_meal_item) {
    private var _binding: FragmentCreateMealItemBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedViewModel: MealViewModel

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
        sharedViewModel = ViewModelProvider(requireActivity())[MealViewModel::class.java]
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
            if (sharedViewModel.isTitleCorrect &&
                sharedViewModel.isInstructionsCorrect
            ) {
                sharedViewModel.createMeal(
                    binding.etTitle.text.toString(),
                    binding.etInstructions.text.toString()
                )
                findNavController().popBackStack()
            } else {
                if (!sharedViewModel.isTitleCorrect) {
                    binding.layoutEtTitle.error = getString(R.string.title_must_not_be_empty)
                }
                if (!sharedViewModel.isInstructionsCorrect) {
                    binding.layoutEtInstructions.error =
                        getString(R.string.instructions_must_not_be_empty)
                }
                false
            }
        }

        binding.etTitle.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_create_meal_item_title, null)
            val dialogBinding = DialogCreateMealItemTitleBinding.bind(view)
            var titleTemp = ""
            dialogBinding.dialogEtTitle.setText("")
            dialogBinding.dialogEtTitle.doOnTextChanged { text, _, _, _ ->
                titleTemp = text.toString()
                val validation = isTitleCorrect(titleTemp)
                if (validation == null) {
                    dialogBinding.dialogLayoutEtTitle.error = null
                } else {
                    dialogBinding.dialogLayoutEtTitle.error = validation
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(getString(R.string.title))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    if (isTitleCorrect(titleTemp) == null) {
                        sharedViewModel.title = titleTemp
                        binding.etTitle.setText(sharedViewModel.title)
                        sharedViewModel.isTitleCorrect = true
                        binding.layoutEtTitle.error = null
                    } else {
                        println(isTitleCorrect(titleTemp))
                    }
                }
                .show()
        }

        binding.etInstructions.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_create_meal_item_instructions, null)
            val dialogBinding = DialogCreateMealItemInstructionsBinding.bind(view)
            var instructionsTemp = ""
            dialogBinding.dialogEtInstructions.setText("")
            dialogBinding.dialogEtInstructions.doOnTextChanged { text, _, _, _ ->
                instructionsTemp = text.toString()
                val validation = isInstructionsCorrect(instructionsTemp)
                if (validation == null) {
                    dialogBinding.dialogLayoutEtInstructions.error = null
                } else {
                    dialogBinding.dialogLayoutEtInstructions.error = validation
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(getString(R.string.instructions))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    if (isInstructionsCorrect(instructionsTemp) == null) {
                        sharedViewModel.instructions = instructionsTemp
                        binding.etInstructions.setText(sharedViewModel.instructions)
                        sharedViewModel.isInstructionsCorrect = true
                        binding.layoutEtInstructions.error = null
                    }
                }
                .show()
        }
    }

    private fun isTitleCorrect(text: String): String? {
        if (text.isBlank()) {
            return getString(R.string.title_must_not_be_empty)
        }
        sharedViewModel.ldMeals.value?.let { mealsWithFoodEntries ->
            println(mealsWithFoodEntries)
            val titleExists = mealsWithFoodEntries.any { mealWithFoodEntries ->
                mealWithFoodEntries.meal.title == text
            }
            if (titleExists) {
                return getString(R.string.meal_with_this_title_already_exists)
            } else {
                return null
            }
        }
        return getString(R.string.title_must_be_valid)
    }

    private fun isInstructionsCorrect(text: String): String? {
        if (text.isBlank()) {
            return getString(R.string.instructions_must_not_be_empty)
        }
        return null
    }
}