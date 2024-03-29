//package com.gasparaiciukas.owntrainer.settings
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.navigation.fragment.findNavController
//import com.gasparaiciukas.owntrainer.R
//import com.gasparaiciukas.owntrainer.databinding.FragmentSettingsBinding
//import com.gasparaiciukas.owntrainer.utils.fragment.setupNavigationView
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//@AndroidEntryPoint
//class SettingsFragment : Fragment(R.layout.fragment_settings) {
//    private var _binding: FragmentSettingsBinding? = null
//    private val binding get() = _binding!!
//
//    lateinit var viewModel: SettingsViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.uiState.collectLatest {
//                    refreshUi(it)
//                }
//            }
//        }
//        initUi()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun initUi() {
//        initNavigation()
//    }
//
//    private fun refreshUi(uiState: SettingsUiState) {
//        setTextViews(uiState)
//        binding.scrollView.visibility = View.VISIBLE
//    }
//
//    private fun setTextViews(uiState: SettingsUiState) {
//        binding.tvAppearance.setOnClickListener {
//            val singleItems = arrayOf(
//                getString(R.string.follow_system),
//                getString(R.string.light_mode),
//                getString(R.string.dark_mode)
//            )
//            val checkedItem = uiState.appearanceMode
//            var selectedItem = uiState.appearanceMode
//
//            MaterialAlertDialogBuilder(requireContext())
//                .setTitle(getString(R.string.appearance))
//                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
//                    dialog.dismiss()
//                    val appearanceMode = AppearanceMode.values()[selectedItem]
//                    viewModel.setAppearanceMode(appearanceMode)
//                    requireActivity().recreate()
//                }
//                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
//                    selectedItem = which
//                }
//                .show()
//        }
//
//        binding.tvProfile.setOnClickListener {
//            findNavController().navigate(
//                SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
//            )
//        }
//
//        binding.tvReminders.setOnClickListener {
//            findNavController().navigate(
//                SettingsFragmentDirections.actionSettingsFragmentToReminderFragment()
//            )
//        }
//    }
//
//    private fun initNavigation() {
//        setupNavigationView(
//            navigationView = binding.navigationView,
//            drawerLayout = binding.drawerLayout,
//            navController = findNavController(),
//            checkedItem = R.id.settingsFragment
//        )
//        binding.topAppBar.setNavigationOnClickListener {
//            binding.drawerLayout.open()
//        }
//    }
//}
