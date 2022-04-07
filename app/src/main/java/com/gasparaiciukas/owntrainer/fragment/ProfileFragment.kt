package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.FragmentProfileBinding
import com.gasparaiciukas.owntrainer.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel.ldUser.observe(viewLifecycleOwner) {
            refreshUi(it)
        }
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
    }

    private fun refreshUi(user: User) {
        setTextFields(user)
        setNavigation(user)
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun isAgeCorrect(s: String): String {
        val age: Int
        try {
            age = s.toInt()
        } catch (e: NumberFormatException) {
            return "Number must be valid"
        }
        if (age <= 0) {
            return "Age must be valid"
        }
        return ""
    }

    private fun isHeightCorrect(s: String): String {
        val height: Int
        try {
            height = s.toInt()
        } catch (e: NumberFormatException) {
            return "Number must be valid"
        }
        if (height <= 0) {
            return "Height must be valid"
        }
        return ""
    }

    private fun isWeightCorrect(s: String): String {
        val weight: Double
        try {
            weight = s.toDouble()
        } catch (e: NumberFormatException) {
            return "Number must be valid"
        }
        if (weight <= 0) {
            return "Height must be valid"
        }
        return ""
    }

    private fun setTextFields(user: User) {
        // Insert current data into fields
        binding.etSex.setText(user.sex)
        binding.etAge.setText(user.ageInYears.toString())
        binding.etHeight.setText(user.heightInCm.toString())
        binding.etWeight.setText(user.weightInKg.toString())
        binding.etLifestyle.setText(user.lifestyle)

        // Set up listeners
        val sexList: List<String?> = ArrayList(listOf("Male", "Female"))
        val sexAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), R.layout.details_list_item, sexList)
        binding.etSex.setAdapter(sexAdapter)
        binding.etAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                val validation = isAgeCorrect(s.toString())
                if (validation == "") {
                    binding.layoutEtAge.error = null
                } else {
                    binding.layoutEtAge.error = validation
                }
            }
        })
        binding.etHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                val validation = isHeightCorrect(s.toString())
                if (validation == "") {
                    binding.layoutEtHeight.error = null
                } else {
                    binding.layoutEtHeight.error = validation
                }
            }
        })
        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                val validation = isWeightCorrect(s.toString())
                if (validation == "") {
                    binding.layoutEtWeight.error = null
                } else {
                    binding.layoutEtWeight.error = validation
                }
            }
        })
        val lifestyleList: List<String?> = ArrayList(
            listOf(
                "Sedentary",
                "Lightly active",
                "Moderately active",
                "Very active",
                "Extra active"
            )
        )
        val lifestyleAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), R.layout.details_list_item, lifestyleList)
        binding.etLifestyle.setAdapter(lifestyleAdapter)
    }

    private fun setNavigation(user: User) {
        binding.topAppBar.menu.findItem(R.id.btn_save).setOnMenuItemClickListener {
            when {
                binding.layoutEtSex.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtSex.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
                binding.layoutEtAge.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtAge.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
                binding.layoutEtHeight.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtHeight.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
                binding.layoutEtWeight.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtWeight.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
                binding.layoutEtLifestyle.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtLifestyle.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
                else -> {
                    viewModel.updateUser(
                        User(
                            userId = user.userId,
                            sex = binding.etSex.text.toString(),
                            ageInYears = binding.etAge.text.toString().toInt(),
                            heightInCm = binding.etHeight.text.toString().toInt(),
                            weightInKg = binding.etWeight.text.toString().toDouble(),
                            lifestyle = binding.etLifestyle.text.toString(),
                            year = user.year,
                            month = user.month,
                            dayOfYear = user.dayOfYear,
                            dayOfMonth = user.dayOfMonth,
                            dayOfWeek = user.dayOfWeek
                        )
                    )
                    true
                }
            }
        }
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.navigationView, findNavController())
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }

}