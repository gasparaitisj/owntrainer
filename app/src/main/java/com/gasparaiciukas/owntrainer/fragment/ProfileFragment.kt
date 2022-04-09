package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.FragmentProfileBinding
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: ProfileViewModel

    private var onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }

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
            return getString(R.string.number_must_be_valid)
        }
        if (age <= 0) {
            return getString(R.string.age_must_be_valid)
        }
        return ""
    }

    private fun isHeightCorrect(s: String): String {
        val height: Int
        try {
            height = s.toInt()
        } catch (e: NumberFormatException) {
            return getString(R.string.number_must_be_valid)
        }
        if (height <= 0) {
            return getString(R.string.height_must_be_valid)
        }
        return ""
    }

    private fun isWeightCorrect(s: String): String {
        val weight: Double
        try {
            weight = s.toDouble()
        } catch (e: NumberFormatException) {
            return getString(R.string.number_must_be_valid)
        }
        if (weight <= 0) {
            return getString(R.string.height_must_be_valid)
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
        binding.etSex.setAdapter(
            ArrayAdapter<Any?>(
                requireContext(),
                R.layout.details_list_item,
                listOf(
                    Constants.Data.SEX_MALE,
                    Constants.Data.SEX_FEMALE
                )
            )
        )
        binding.etAge.doOnTextChanged { text, _, _, _ ->
            val validation = isAgeCorrect(text.toString())
            if (validation == "") {
                binding.layoutEtAge.error = null
            } else {
                binding.layoutEtAge.error = validation
            }
        }
        binding.etHeight.doOnTextChanged { text, _, _, _ ->
            val validation = isHeightCorrect(text.toString())
            if (validation == "") {
                binding.layoutEtHeight.error = null
            } else {
                binding.layoutEtHeight.error = validation
            }
        }
        binding.etWeight.doOnTextChanged { text, _, _, _ ->
            val validation = isWeightCorrect(text.toString())
            if (validation == "") {
                binding.layoutEtWeight.error = null
            } else {
                binding.layoutEtWeight.error = validation
            }
        }
        binding.etLifestyle.setAdapter(
            ArrayAdapter<Any?>(
                requireContext(),
                R.layout.details_list_item,
                listOf(
                    Constants.Data.LIFESTYLE_SEDENTARY,
                    Constants.Data.LIFESTYLE_LIGHTLY_ACTIVE,
                    Constants.Data.LIFESTYLE_MODERATELY_ACTIVE,
                    Constants.Data.LIFESTYLE_VERY_ACTIVE,
                    Constants.Data.LIFESTYLE_EXTRA_ACTIVE
                )
            )
        )
    }


    private fun setNavigation(user: User) {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = when {
                binding.layoutEtSex.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtSex.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.layoutEtAge.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtAge.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.layoutEtHeight.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtHeight.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.layoutEtWeight.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtWeight.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.layoutEtLifestyle.error != null -> {
                    Toast.makeText(
                        requireContext(),
                        binding.layoutEtLifestyle.error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
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
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
        }
    }
}