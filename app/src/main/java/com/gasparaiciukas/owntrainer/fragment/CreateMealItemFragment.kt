package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentCreateMealItemBinding
import io.realm.Realm

class CreateMealItemFragment : Fragment() {
    private var _binding: FragmentCreateMealItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateMealItemBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        // Back button
        binding.appBar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        // Save button
        binding.appBar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.create_meal_top_app_bar_save) {
                // Parse text edits
                val title: String =
                    when {
                        binding.etTitle.text == null -> "No title"
                        binding.etTitle.text.toString().trim { it <= ' ' }
                            .isEmpty() -> "No title"
                        else -> binding.etTitle.text.toString()
                    }
                val instructions: String =
                    when {
                        binding.etInstructions.text == null -> "No instructions"
                        binding.etInstructions.text.toString().trim { it <= ' ' }
                            .isEmpty() -> "No title"
                        else -> binding.etInstructions.text.toString()
                    }

                // Add meal to database
                val meal = Meal()
                meal.title = title
                meal.instructions = instructions
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { r -> r.insertOrUpdate(meal) }
                realm.close()

                // Finish fragment
                findNavController().popBackStack()
                return@OnMenuItemClickListener true
            }
            false
        })
    }
}