package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import io.realm.Realm
import timber.log.Timber
import java.time.LocalDate
import kotlin.math.roundToInt

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MealAdapter
    private lateinit var supportFragmentManager: FragmentManager

    private val viewModel: DiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager = requireActivity().supportFragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Timber.d("DiaryFragment is created!")
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("DiaryFragment is destroyed!")
        _binding = null
    }

    private fun initUi() {
        // Navigation
        binding.tvDayOfWeek.text = DateFormatter.dayOfWeekToString(viewModel.diaryEntry.dayOfWeek)
        binding.tvMonthOfYear.text = DateFormatter.monthOfYearToString(viewModel.diaryEntry.monthOfYear)
        binding.tvDayOfMonth.text = viewModel.diaryEntry.dayOfMonth.toString()

        // Intakes
        binding.tvCaloriesIntake.text = viewModel.user.dailyKcalIntake.roundToInt().toString()
        binding.tvProteinIntake.text = viewModel.user.dailyProteinIntakeInG.roundToInt().toString()
        binding.tvFatIntake.text = viewModel.user.dailyFatIntakeInG.roundToInt().toString()
        binding.tvCarbsIntake.text = viewModel.user.dailyCarbsIntakeInG.roundToInt().toString()

        // Total calories of day
        binding.tvCaloriesConsumed.text = viewModel.caloriesConsumed.roundToInt().toString()
        binding.tvProteinConsumed.text = viewModel.proteinConsumed.roundToInt().toString()
        binding.tvFatConsumed.text = viewModel.fatConsumed.roundToInt().toString()
        binding.tvCarbsConsumed.text = viewModel.carbsConsumed.roundToInt().toString()

        // Percentage of daily intake
        binding.tvCaloriesPercentage.text = viewModel.caloriesPercentage.roundToInt().toString()
        binding.tvProteinPercentage.text = viewModel.proteinPercentage.roundToInt().toString()
        binding.tvFatPercentage.text = viewModel.fatPercentage.roundToInt().toString()
        binding.tvCarbsPercentage.text = viewModel.carbsPercentage.roundToInt().toString()

        // Add meal to diary on FAB clicked
        binding.fab.setOnClickListener {
            val action = DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment(
                    viewModel.diaryEntry.yearAndDayOfYear.toString()
                )
            findNavController().navigate(action)
        }

        // Navigation (back button)
        binding.btnBack.setOnClickListener {
            // Refresh fragment and show previous day
            viewModel.updateUserToPreviousDay()
            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
            findNavController().navigate(action)
        }

        // Navigation (date layout)
        binding.layoutDate.setOnClickListener {
            // Refresh fragment and show current day
            viewModel.updateUserToCurrentDay()
            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
            findNavController().navigate(action)
        }

        // Navigation (forward button)
        binding.btnForward.setOnClickListener {
            // Refresh fragment and show next day
            viewModel.updateUserToNextDay()
            val action = DiaryFragmentDirections.actionDiaryFragmentSelf()
            findNavController().navigate(action)
        }
    }

    private fun initRecyclerView() {
        // Set up recycler view
        val layoutManager = LinearLayoutManager(context)
        val passLambda: (_1: Meal, _2: Int) -> Unit = { _: Meal, _: Int -> }
        adapter = MealAdapter(viewModel.diaryEntry.meals, passLambda)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }
}