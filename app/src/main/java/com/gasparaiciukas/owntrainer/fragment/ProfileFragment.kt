package com.gasparaiciukas.owntrainer.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Lifestyle
import com.gasparaiciukas.owntrainer.database.Sex
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.DialogProfileAgeBinding
import com.gasparaiciukas.owntrainer.databinding.DialogProfileHeightBinding
import com.gasparaiciukas.owntrainer.databinding.DialogProfileWeightBinding
import com.gasparaiciukas.owntrainer.databinding.FragmentProfileBinding
import com.gasparaiciukas.owntrainer.utils.Constants.AGE_MAXIMUM
import com.gasparaiciukas.owntrainer.utils.Constants.AGE_MINIMUM
import com.gasparaiciukas.owntrainer.utils.Constants.HEIGHT_MAXIMUM
import com.gasparaiciukas.owntrainer.utils.Constants.HEIGHT_MINIMUM
import com.gasparaiciukas.owntrainer.utils.Constants.WEIGHT_MAXIMUM
import com.gasparaiciukas.owntrainer.utils.Constants.WEIGHT_MINIMUM
import com.gasparaiciukas.owntrainer.utils.discard
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedViewModel: SettingsViewModel

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
        sharedViewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]

        sharedViewModel.ldUser.value?.let { user ->
            initUi(user)
        }

        sharedViewModel.ldUser.observe(viewLifecycleOwner) {
            initUi(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi(user: User) {
        setTextFields(user)
        setNavigation(user)
        binding.scrollView.visibility = View.VISIBLE
    }

    @SuppressLint("InflateParams")
    private fun setTextFields(user: User) {
        // Insert current data into fields
        binding.etSex.setText(Sex.values()[user.sex].selectionDescription(requireContext()))
        binding.etAge.setText(user.ageInYears.toString())
        binding.etHeight.setText(user.heightInCm.toString())
        binding.etWeight.setText(user.weightInKg.toString())
        binding.etLifestyle.setText(
            Lifestyle.values()[user.lifestyle].selectionDescription(
                requireContext()
            )
        )

        binding.etSex.setOnClickListener {
            val singleItems = arrayOf(
                getString(R.string.male),
                getString(R.string.female)
            )
            val checkedItem = user.sex
            var selectedItem = user.sex

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.sex))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    user.sex = selectedItem
                    sharedViewModel.updateUser(user)
                }
                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                    selectedItem = which
                }
                .show()
        }
        binding.etAge.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_profile_age, null)
            val dialogBinding = DialogProfileAgeBinding.bind(view)
            var age = user.ageInYears.toString()
            dialogBinding.dialogEtAge.setText(age)
            dialogBinding.dialogEtAge.doOnTextChanged { text, _, _, _ ->
                age = text.toString()
                val validation = isAgeCorrect(age)
                if (validation == null) {
                    dialogBinding.dialogLayoutEtAge.error = null
                } else {
                    dialogBinding.dialogLayoutEtAge.error = validation
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(getString(R.string.age))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    if (isAgeCorrect(age) == null) {
                        user.ageInYears = age.toInt()
                        sharedViewModel.updateUser(user)
                    }
                }
                .show()
        }

        binding.etHeight.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_profile_height, null)
            val dialogBinding = DialogProfileHeightBinding.bind(view)
            var height = user.heightInCm.toString()
            dialogBinding.dialogEtHeight.setText(height)
            dialogBinding.dialogEtHeight.doOnTextChanged { text, _, _, _ ->
                height = text.toString()
                val validation = isHeightCorrect(height)
                if (validation == null) {
                    dialogBinding.dialogLayoutEtHeight.error = null
                } else {
                    dialogBinding.dialogLayoutEtHeight.error = validation
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(getString(R.string.height))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    if (isHeightCorrect(height) == null) {
                        user.heightInCm = height.toInt()
                        sharedViewModel.updateUser(user)
                    }
                }
                .show()
        }
        binding.etWeight.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_profile_weight, null)
            val dialogBinding = DialogProfileWeightBinding.bind(view)
            var weight = user.weightInKg.toString()
            dialogBinding.dialogEtWeight.setText(weight)
            dialogBinding.dialogEtWeight.doOnTextChanged { text, _, _, _ ->
                weight = text.toString()
                val validation = isWeightCorrect(weight)
                if (validation == null) {
                    dialogBinding.dialogLayoutEtWeight.error = null
                } else {
                    dialogBinding.dialogLayoutEtWeight.error = validation
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(getString(R.string.weight))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    if (isWeightCorrect(weight) == null) {
                        user.weightInKg = weight.toDouble()
                        sharedViewModel.updateUser(user)
                    }
                }
                .show()
        }

        binding.etLifestyle.setOnClickListener {
            val singleItems = arrayOf(
                getString(R.string.sedentary),
                getString(R.string.lightly_active),
                getString(R.string.moderately_active),
                getString(R.string.very_active),
                getString(R.string.extra_active)
            )
            val checkedItem = user.lifestyle
            var selectedItem = user.lifestyle

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.lifestyle))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    user.lifestyle = selectedItem
                    sharedViewModel.updateUser(user)
                }
                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                    selectedItem = which
                }
                .show()
        }
    }

    private fun setNavigation(user: User) {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed(user)
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = onBackPressed(user)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun onBackPressed(user: User) {
        if (binding.layoutEtSex.error == null ||
            binding.layoutEtAge.error == null ||
            binding.layoutEtHeight.error == null ||
            binding.layoutEtWeight.error == null ||
            binding.layoutEtLifestyle.error == null
        ) {
            findNavController().popBackStack().discard()
        } else {
            val sex = Sex.values().find { it.value == binding.etSex.text.toString() }
                ?: Sex.MALE
            val lifestyle =
                Lifestyle.values().find { it.value == binding.etLifestyle.text.toString() }
                    ?: Lifestyle.SEDENTARY
            sharedViewModel.updateUser(
                User(
                    userId = user.userId,
                    sex = sex.ordinal,
                    ageInYears = binding.etAge.text.toString().toInt(),
                    heightInCm = binding.etHeight.text.toString().toInt(),
                    weightInKg = binding.etWeight.text.toString().toDouble(),
                    lifestyle = lifestyle.ordinal,
                    year = user.year,
                    month = user.month,
                    dayOfYear = user.dayOfYear,
                    dayOfMonth = user.dayOfMonth,
                    dayOfWeek = user.dayOfWeek
                )
            )
            findNavController().popBackStack().discard()
        }
    }

    private fun isAgeCorrect(s: String): String? {
        val age: Int
        try {
            age = s.toInt()
        } catch (e: NumberFormatException) {
            return getString(R.string.number_must_be_valid)
        }
        if ((age in AGE_MINIMUM..AGE_MAXIMUM).not()) {
            return if (age in 1..17) {
                getString(R.string.age_too_young_alert)
            } else {
                getString(R.string.age_must_be_valid)
            }
        }
        return null
    }

    private fun isHeightCorrect(s: String): String? {
        val height: Int
        try {
            height = s.toInt()
        } catch (e: NumberFormatException) {
            return getString(R.string.number_must_be_valid)
        }
        if (((height in HEIGHT_MINIMUM..HEIGHT_MAXIMUM).not())) {
            return getString(R.string.height_must_be_valid)
        }
        return null
    }

    private fun isWeightCorrect(s: String): String? {
        val weight: Double
        try {
            weight = s.toDouble()
        } catch (e: NumberFormatException) {
            return getString(R.string.number_must_be_valid)
        }
        if (((weight.roundToInt() in WEIGHT_MINIMUM..WEIGHT_MAXIMUM).not())) {
            return getString(R.string.weight_must_be_valid)
        }
        return null
    }
}