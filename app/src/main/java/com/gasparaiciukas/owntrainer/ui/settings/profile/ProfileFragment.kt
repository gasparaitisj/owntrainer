package com.gasparaiciukas.owntrainer.ui.settings.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.DialogProfileAgeBinding
import com.gasparaiciukas.owntrainer.databinding.DialogProfileHeightBinding
import com.gasparaiciukas.owntrainer.databinding.DialogProfileWeightBinding
import com.gasparaiciukas.owntrainer.databinding.FragmentProfileBinding
import com.gasparaiciukas.owntrainer.settings.SettingsUiState
import com.gasparaiciukas.owntrainer.settings.SettingsViewModel
import com.gasparaiciukas.owntrainer.utils.database.Lifestyle
import com.gasparaiciukas.owntrainer.utils.database.Sex
import com.gasparaiciukas.owntrainer.utils.database.User
import com.gasparaiciukas.owntrainer.utils.discard
import com.gasparaiciukas.owntrainer.utils.other.Constants.AGE_MAXIMUM
import com.gasparaiciukas.owntrainer.utils.other.Constants.AGE_MINIMUM
import com.gasparaiciukas.owntrainer.utils.other.Constants.HEIGHT_MAXIMUM
import com.gasparaiciukas.owntrainer.utils.other.Constants.HEIGHT_MINIMUM
import com.gasparaiciukas.owntrainer.utils.other.Constants.WEIGHT_MAXIMUM
import com.gasparaiciukas.owntrainer.utils.other.Constants.WEIGHT_MINIMUM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.uiState.collectLatest {
                    initUi(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi(uiState: SettingsUiState) {
        setTextFields(uiState)
        setNavigation(uiState)
        binding.scrollView.visibility = View.VISIBLE
    }

    @SuppressLint("InflateParams")
    private fun setTextFields(uiState: SettingsUiState) {
        // Insert current data into fields
        binding.etSex.setText(Sex.values()[uiState.user.sex].selectionDescription(requireContext()))
        binding.etAge.setText(uiState.user.ageInYears.toString())
        binding.etHeight.setText(uiState.user.heightInCm.toString())
        binding.etWeight.setText(uiState.user.weightInKg.toString())
        binding.etLifestyle.setText(
            Lifestyle.values()[uiState.user.lifestyle].selectionDescription(
                requireContext()
            )
        )

        binding.etSex.setOnClickListener {
            val singleItems = arrayOf(
                getString(R.string.male),
                getString(R.string.female)
            )
            val checkedItem = uiState.user.sex
            var selectedItem = uiState.user.sex

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.sex))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    uiState.user.sex = selectedItem
                    sharedViewModel.updateUser(uiState.user)
                }
                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                    selectedItem = which
                }
                .show()
        }
        binding.etAge.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_profile_age, null)
            val dialogBinding = DialogProfileAgeBinding.bind(view)
            var age = uiState.user.ageInYears.toString()
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
                        uiState.user.ageInYears = age.toInt()
                        sharedViewModel.updateUser(uiState.user)
                    }
                }
                .show()
        }

        binding.etHeight.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_profile_height, null)
            val dialogBinding = DialogProfileHeightBinding.bind(view)
            var height = uiState.user.heightInCm.toString()
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
                        uiState.user.heightInCm = height.toInt()
                        sharedViewModel.updateUser(uiState.user)
                    }
                }
                .show()
        }
        binding.etWeight.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_profile_weight, null)
            val dialogBinding = DialogProfileWeightBinding.bind(view)
            var weight = uiState.user.weightInKg.toString()
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
                        uiState.user.weightInKg = weight.toDouble()
                        sharedViewModel.updateUser(uiState.user)
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
            val checkedItem = uiState.user.lifestyle
            var selectedItem = uiState.user.lifestyle

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.lifestyle))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    uiState.user.lifestyle = selectedItem
                    sharedViewModel.updateUser(uiState.user)
                }
                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                    selectedItem = which
                }
                .show()
        }
    }

    private fun setNavigation(uiState: SettingsUiState) {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed(uiState)
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = onBackPressed(uiState)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun onBackPressed(uiState: SettingsUiState) {
        if (binding.layoutEtSex.error == null ||
            binding.layoutEtAge.error == null ||
            binding.layoutEtHeight.error == null ||
            binding.layoutEtWeight.error == null ||
            binding.layoutEtLifestyle.error == null
        ) {
            findNavController().popBackStack().discard()
        } else {
            val sex = Sex.values().find {
                it.selectionDescription(requireContext()) == binding.etSex.text.toString()
            } ?: Sex.MALE
            val lifestyle = Lifestyle.values().find {
                it.selectionDescription(requireContext()) == binding.etLifestyle.text.toString()
            } ?: Lifestyle.SEDENTARY
            sharedViewModel.updateUser(
                User(
                    userId = uiState.user.userId,
                    sex = sex.ordinal,
                    ageInYears = binding.etAge.text.toString().toInt(),
                    heightInCm = binding.etHeight.text.toString().toInt(),
                    weightInKg = binding.etWeight.text.toString().toDouble(),
                    lifestyle = lifestyle.ordinal,
                    year = uiState.user.year,
                    month = uiState.user.month,
                    dayOfYear = uiState.user.dayOfYear,
                    dayOfMonth = uiState.user.dayOfMonth,
                    dayOfWeek = uiState.user.dayOfWeek
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
