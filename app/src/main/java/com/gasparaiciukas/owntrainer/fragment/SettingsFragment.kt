package com.gasparaiciukas.owntrainer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentSettingsBinding
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        setTextFields()
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun setTextFields() {
        binding.switchDarkMode.isChecked = isDarkMode()
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setDarkMode(true)
            } else {
                setDarkMode(false)
            }
        }
    }

    fun setDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
        requireActivity()
            .getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("darkMode", isDarkMode)
            .apply()
    }

    private fun isDarkMode(): Boolean {
        return requireActivity()
            .getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getBoolean("darkMode", false)
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.navigationView, findNavController())
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }
}