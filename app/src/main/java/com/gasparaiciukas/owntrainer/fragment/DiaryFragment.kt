package com.gasparaiciukas.owntrainer.fragment

import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.activity.AddMealToDiaryActivity
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.databinding.FragmentDiaryBinding
import io.realm.Realm
import java.time.LocalDate
import kotlin.math.roundToInt
//TODO: Fix FAB action and back button
class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MealAdapter
    private lateinit var realm: Realm
    private lateinit var diaryEntry: DiaryEntry
    private lateinit var user: User
    private lateinit var currentDay: LocalDate
    private lateinit var supportFragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager = requireActivity().supportFragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        getDiaryEntry()
        val rootView = binding.root
        initUi(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        adapter.reload()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun getDiaryEntry() {
        // Get user from database
        realm = Realm.getDefaultInstance()
        //TODO: Fix NPE when user doesn't complete app intro
        user = realm.where(User::class.java).findFirst()!!

        // Get day to be showed from preferences
        val sharedPreferences = requireActivity()
                .getSharedPreferences("diary", Context.MODE_PRIVATE)
        val year = sharedPreferences.getInt("year", 0)
        val month = sharedPreferences.getInt("month", 0)
        val day = sharedPreferences.getInt("day", 0)
        currentDay = LocalDate.of(year, month, day)

        // Try to get diary entry from database
        val diaryEntryFromDatabase = realm.where(DiaryEntry::class.java)
                .equalTo("yearAndDayOfYear", currentDay.year.toString() + currentDay.dayOfYear)
                .findFirst()

        // If current day's entry does not exist, insert it to the database
        if (diaryEntryFromDatabase == null) {
            val newDiaryEntry = DiaryEntry()
            newDiaryEntry.yearAndDayOfYear = currentDay.year.toString() + currentDay.dayOfYear
            newDiaryEntry.year = currentDay.year
            newDiaryEntry.dayOfYear = currentDay.dayOfYear
            newDiaryEntry.dayOfMonth = currentDay.dayOfMonth
            newDiaryEntry.dayOfWeek = currentDay.dayOfWeek.value
            newDiaryEntry.monthOfYear = currentDay.monthValue
            realm.executeTransaction { realm -> realm.insertOrUpdate(newDiaryEntry) }
            diaryEntry = newDiaryEntry
        } else {
            diaryEntry = diaryEntryFromDatabase
        }
    }

    private fun initUi(rootView: View) {
        // Navigation
        binding.tvDayOfWeek.text = dayOfWeekToString(diaryEntry.dayOfWeek)
        binding.tvMonthOfYear.text = monthOfYearToString(diaryEntry.monthOfYear)
        binding.tvDayOfMonth.text = diaryEntry.dayOfMonth.toString()

        // Intakes
        binding.tvCaloriesIntake.text = user.dailyKcalIntake.roundToInt().toString()
        binding.tvProteinIntake.text = user.dailyProteinIntakeInG.roundToInt().toString()
        binding.tvFatIntake.text = user.dailyFatIntakeInG.roundToInt().toString()
        binding.tvCarbsIntake.text = user.dailyCarbsIntakeInG.roundToInt().toString()

        // Total calories of day
        val caloriesConsumed = diaryEntry.calculateTotalCalories(diaryEntry.meals)
        val proteinConsumed = diaryEntry.calculateTotalProtein(diaryEntry.meals)
        val fatConsumed = diaryEntry.calculateTotalFat(diaryEntry.meals)
        val carbsConsumed = diaryEntry.calculateTotalCarbs(diaryEntry.meals)
        binding.tvCaloriesConsumed.text = caloriesConsumed.roundToInt().toString()
        binding.tvProteinConsumed.text = proteinConsumed.roundToInt().toString()
        binding.tvFatConsumed.text = fatConsumed.roundToInt().toString()
        binding.tvCarbsConsumed.text = carbsConsumed.roundToInt().toString()

        // Percentage of daily intake
        val caloriesPercentage = (caloriesConsumed / user.dailyKcalIntake) * 100
        val proteinPercentage = (proteinConsumed / user.dailyProteinIntakeInG) * 100
        val fatPercentage = (fatConsumed / user.dailyFatIntakeInG) * 100
        val carbsPercentage = (carbsConsumed / user.dailyCarbsIntakeInG) * 100
        binding.tvCaloriesPercentage.text = caloriesPercentage.roundToInt().toString()
        binding.tvProteinPercentage.text = proteinPercentage.roundToInt().toString()
        binding.tvFatPercentage.text = fatPercentage.roundToInt().toString()
        binding.tvCarbsPercentage.text = carbsPercentage.roundToInt().toString()

        // Add meal to diary on FAB clicked
        binding.fab.setOnClickListener {
            val intent = Intent(rootView.context, AddMealToDiaryActivity::class.java)
            intent.putExtra("primaryKey", diaryEntry.yearAndDayOfYear)
            requireContext().startActivity(intent)
        }

        // Navigation (back button)
        binding.btnBack.setOnClickListener {
            // Save previous day to preferences
            val sharedPreferences = requireActivity()
                    .getSharedPreferences("diary", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val previousDay = currentDay.minusDays(1) // subtract 1 day from current day
            editor.putInt("year", previousDay.year)
            editor.putInt("month", previousDay.monthValue)
            editor.putInt("day", previousDay.dayOfMonth)
            editor.apply()

            // Refresh fragment and show previous day
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.layout_frame_fragment, newInstance())
            }
        }

        // Navigation (back button)
        binding.btnForward.setOnClickListener {
            // Save next day to preferences
            val sharedPreferences = requireActivity()
                    .getSharedPreferences("diary", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val nextDay = currentDay.plusDays(1)
            editor.putInt("year", nextDay.year)
            editor.putInt("month", nextDay.monthValue)
            editor.putInt("day", nextDay.dayOfMonth)
            editor.apply()

            // Refresh fragment and show next day
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.layout_frame_fragment, newInstance())
            }
        }
    }

    private fun initRecyclerView() {
        // Set up recycler view
        val layoutManager = LinearLayoutManager(context)
        adapter = MealAdapter(4, diaryEntry.meals)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    private fun dayOfWeekToString(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "Mon"
            2 -> "Tue"
            3 -> "Wed"
            4 -> "Thu"
            5 -> "Fri"
            6 -> "Sat"
            7 -> "Sun"
            else -> ""
        }
    }

    private fun monthOfYearToString(monthOfYear: Int): String {
        return when (monthOfYear) {
            1 -> " January "
            2 -> " February "
            3 -> " March "
            4 -> " April "
            5 -> " May "
            6 -> " June "
            7 -> " July "
            8 -> " August "
            9 -> " September "
            10 -> " October "
            11 -> " November "
            12 -> " December "
            else -> ""
        }
    }

    companion object {
        fun newInstance(): DiaryFragment {
            return DiaryFragment()
        }
    }
}