package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import com.gasparaiciukas.owntrainer.utils.DateFormatter
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import timber.log.Timber
import kotlin.math.roundToInt

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MealAdapter

    private val viewModel: DiaryViewModel by viewModels()

    private val singleClickListener: (meal: Meal, position: Int) -> Unit = { _: Meal, position: Int ->
        val action = DiaryFragmentDirections.actionDiaryFragmentToMealItemFragment(position)
        findNavController().navigate(action)
    }

    private val longClickListener: (position: Int) -> Unit = { position: Int ->
        Timber.d("Position: $position")
        viewModel.deleteMealFromDiary(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, (adapter.itemCount - position))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        viewModel.dataChanged.observe(viewLifecycleOwner, Observer {
            setTextViews()
        })
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
        setTextViews()
        setListeners()
        initRecyclerView()
    }

    private fun setTextViews() {
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
    }

    private fun setListeners() {
        // Add meal to diary on FAB clicked
        binding.fab.setOnClickListener {
            val action = DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment(
                viewModel.diaryEntry.yearAndDayOfYear
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
        val layoutManager = LinearLayoutManager(context)
        val passLambda: (_1: Meal, _2: Int) -> Unit = { _: Meal, _: Int -> }
        adapter = MealAdapter(viewModel.diaryEntry.meals, singleClickListener, longClickListener)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }
}