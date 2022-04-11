package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentSettingsBinding
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.viewmodel.AppearanceMode
import com.gasparaiciukas.owntrainer.viewmodel.SettingsUiState
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
        viewModel.ldUser.observe(viewLifecycleOwner) {
            viewModel.loadUiState()
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
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

    private fun refreshUi(uiState: SettingsUiState) {
        setTextViews(uiState)
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun setTextViews(uiState: SettingsUiState) {
        binding.tvAppearance.setOnClickListener {
            val singleItems = arrayOf(
                getString(R.string.follow_system),
                getString(R.string.light_mode),
                getString(R.string.dark_mode)
            )
            val checkedItem = uiState.appearanceMode
            var selectedItem = uiState.appearanceMode

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.appearance))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    val appearanceMode = AppearanceMode.values()[selectedItem]
                    viewModel.setAppearanceMode(appearanceMode)
                    requireActivity().recreate()
                }
                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                    selectedItem = which
                }
                .show()
        }

        binding.tvProfile.setOnClickListener {
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
            )
        }
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.navigationView, findNavController())
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }
}