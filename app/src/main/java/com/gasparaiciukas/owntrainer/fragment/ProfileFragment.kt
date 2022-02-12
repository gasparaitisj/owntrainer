package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentProfileBinding
import com.gasparaiciukas.owntrainer.viewmodel.ProfileViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ProfileViewModel>()

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
        viewModel.ldUser.observe(viewLifecycleOwner) {
            viewModel.user = it
            initUi()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        setTextFields()
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

    private fun setTextFields() {
        // Insert current data into fields
        binding.etSex.setText(viewModel.user.sex)
        binding.etAge.setText(viewModel.user.ageInYears.toString())
        binding.etHeight.setText(viewModel.user.heightInCm.toString())
        binding.etWeight.setText(viewModel.user.weightInKg.toString())
        binding.etLifestyle.setText(viewModel.user.lifestyle)

        // Set up listeners
        val sexList: List<String?> = ArrayList(listOf("Male", "Female"))
        val sexAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), R.layout.details_list_item, sexList)
        binding.etSex.setAdapter(sexAdapter)
        binding.etSex.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                viewModel.user.sex = binding.etSex.text.toString()
            }
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
                    viewModel.user.ageInYears = s.toString().toInt()
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
                    viewModel.user.heightInCm = s.toString().toInt()
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
                    viewModel.user.weightInKg = s.toString().toDouble()
                    binding.layoutEtWeight.error = null
                } else {
                    binding.layoutEtWeight.error = validation
                }
            }
        })
        binding.etLifestyle.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                viewModel.user.lifestyle = binding.etLifestyle.text.toString()
            }
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

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.topAppBar.menu.findItem(R.id.btn_save).setOnMenuItemClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.writeUserToDatabase()
            }
            return@setOnMenuItemClickListener true
        }

        binding.navigationView.setCheckedItem(R.id.profile)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.home -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToDiaryFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.foods -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToFoodFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.meals -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToMealFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.progress -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToProgressFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.profile -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action = ProfileFragmentDirections.actionProfileFragmentSelf()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
                R.id.settings -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            super.onDrawerClosed(drawerView)
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
                            findNavController().navigate(action)
                        }
                    })
                    binding.drawerLayout.close()
                }
            }
            true
        }
    }

    private fun slideBottomNavigationUp() {
        val botNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val layoutParams = botNav?.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = layoutParams.behavior
            if (behavior is HideBottomViewOnScrollBehavior) {
                behavior.slideUp(botNav)
            }
        }
    }

    private fun slideBottomNavigationDown() {
        val botNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val layoutParams = botNav?.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = layoutParams.behavior
            if (behavior is HideBottomViewOnScrollBehavior) {
                behavior.slideDown(botNav)
            }
        }
    }

}