package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentSettingsBinding
import com.gasparaiciukas.owntrainer.viewmodel.ProfileViewModel
import com.gasparaiciukas.owntrainer.viewmodel.SettingsUiState
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        viewModel.ldIsDarkMode.observe(viewLifecycleOwner) {
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
        binding.switchDarkMode.isChecked = uiState.isDarkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
            requireActivity().recreate()
        }
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.navigationView, findNavController())
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }
}