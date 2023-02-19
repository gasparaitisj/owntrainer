package com.gasparaiciukas.owntrainer.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentProgressBinding
import com.gasparaiciukas.owntrainer.ui.progress.ProgressScreen
import com.gasparaiciukas.owntrainer.utils.fragment.setupBottomNavigation
import com.gasparaiciukas.owntrainer.utils.fragment.setupNavigationView

class ProgressFragment : Fragment(R.layout.fragment_progress) {
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ProgressScreen()
        }
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
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun initNavigation() {
        setupBottomNavigation(
            bottomNavigation = binding.bottomNavigation,
            navController = findNavController(),
            checkedItemId = R.id.progressFragment
        )
        setupNavigationView(
            navigationView = binding.navigationView,
            drawerLayout = binding.drawerLayout,
            navController = findNavController(),
            checkedItem = R.id.progressFragment
        )
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }
}
