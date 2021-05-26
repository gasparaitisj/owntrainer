package com.gasparaiciukas.owntrainer.fragment

import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.activity.AddMealToDiaryActivity
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import java.time.LocalDate
import kotlin.math.roundToInt

class DiaryFragment : Fragment() {
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
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_diary, container, false)

        getDiaryEntry()
        initUi(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(view)
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
        // Navigation UI
        val backButton = rootView.findViewById<ImageButton>(R.id.diary_back_button)
        val forwardButton = rootView.findViewById<ImageButton>(R.id.diary_forward_button)
        val tDayOfWeek = rootView.findViewById<TextView>(R.id.diary_current_day_of_week)
        val tMonthOfYear = rootView.findViewById<TextView>(R.id.diary_current_month_of_year)
        val tDayOfMonth = rootView.findViewById<TextView>(R.id.diary_current_day_of_month)

        // Nutrition UI
        val tDailyCaloriesPercentage = rootView.findViewById<TextView>(R.id.diary_daily_goal_calories_percentage)
        val tDailyCaloriesConsumed = rootView.findViewById<TextView>(R.id.diary_daily_goal_calories_consumed)
        val tDailyCaloriesIntake = rootView.findViewById<TextView>(R.id.diary_daily_goal_calories_intake)
        val tDailyProteinPercentage = rootView.findViewById<TextView>(R.id.diary_daily_goal_protein_percentage)
        val tDailyProteinConsumed = rootView.findViewById<TextView>(R.id.diary_daily_goal_protein_consumed)
        val tDailyProteinIntake = rootView.findViewById<TextView>(R.id.diary_daily_goal_protein_intake)
        val tDailyFatPercentage = rootView.findViewById<TextView>(R.id.diary_daily_goal_fat_percentage)
        val tDailyFatConsumed = rootView.findViewById<TextView>(R.id.diary_daily_goal_fat_consumed)
        val tDailyFatIntake = rootView.findViewById<TextView>(R.id.diary_daily_goal_fat_intake)
        val tDailyCarbsPercentage = rootView.findViewById<TextView>(R.id.diary_daily_goal_carbs_percentage)
        val tDailyCarbsConsumed = rootView.findViewById<TextView>(R.id.diary_daily_goal_carbs_consumed)
        val tDailyCarbsIntake = rootView.findViewById<TextView>(R.id.diary_daily_goal_carbs_intake)

        // Navigation
        tDayOfWeek.text = dayOfWeekToString(diaryEntry.dayOfWeek)
        tMonthOfYear.text = monthOfYearToString(diaryEntry.monthOfYear)
        tDayOfMonth.text = diaryEntry.dayOfMonth.toString()

        // Intakes
        val dailyKcalIntake = user.dailyKcalIntake
        val dailyProteinIntakeInG = user.dailyProteinIntakeInG
        val dailyFatIntakeInG = user.dailyFatIntakeInG
        val dailyCarbsIntakeInG = user.dailyCarbsIntakeInG
        tDailyCaloriesIntake.text = dailyKcalIntake.roundToInt().toString()
        tDailyProteinIntake.text = dailyProteinIntakeInG.roundToInt().toString()
        tDailyFatIntake.text = dailyFatIntakeInG.roundToInt().toString()
        tDailyCarbsIntake.text = dailyCarbsIntakeInG.roundToInt().toString()

        // Total calories of day
        val caloriesConsumed = diaryEntry.calculateTotalCalories(diaryEntry.meals)
        val proteinConsumed = diaryEntry.calculateTotalProtein(diaryEntry.meals)
        val fatConsumed = diaryEntry.calculateTotalFat(diaryEntry.meals)
        val carbsConsumed = diaryEntry.calculateTotalCarbs(diaryEntry.meals)
        tDailyCaloriesConsumed.text = caloriesConsumed.roundToInt().toString()
        tDailyProteinConsumed.text = proteinConsumed.roundToInt().toString()
        tDailyFatConsumed.text = fatConsumed.roundToInt().toString()
        tDailyCarbsConsumed.text = carbsConsumed.roundToInt().toString()

        // Percentage of daily intake
        val caloriesPercentage = caloriesConsumed / (dailyKcalIntake * 100)
        val proteinPercentage = proteinConsumed / (dailyProteinIntakeInG * 100)
        val fatPercentage = fatConsumed / (dailyFatIntakeInG * 100)
        val carbsPercentage = carbsConsumed / (dailyCarbsIntakeInG * 100)
        tDailyCaloriesPercentage.text = caloriesPercentage.roundToInt().toString()
        tDailyProteinPercentage.text = proteinPercentage.roundToInt().toString()
        tDailyFatPercentage.text = fatPercentage.roundToInt().toString()
        tDailyCarbsPercentage.text = carbsPercentage.roundToInt().toString()

        // Add meal to diary on FAB clicked
        val fab = rootView.findViewById<FloatingActionButton>(R.id.diary_fab_add_meal)
        fab.setOnClickListener {
            val intent = Intent(rootView.context, AddMealToDiaryActivity::class.java)
            intent.putExtra("primaryKey", diaryEntry.yearAndDayOfYear)
            requireContext().startActivity(intent)
        }

        // Navigation (back button)
        backButton.setOnClickListener {
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
                replace(R.id.main_fragment_frame_layout, newInstance())
            }
        }

        // Navigation (back button)
        forwardButton.setOnClickListener {
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
                replace(R.id.main_fragment_frame_layout, newInstance())
            }
        }
    }

    private fun initRecyclerView(view: View) {
        // Set up recycler view
        val recyclerView = view.findViewById<RecyclerView>(R.id.diary_meal_recycler_view)
        val layoutManager = LinearLayoutManager(context)
        adapter = MealAdapter(4, diaryEntry.meals)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
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
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

    companion object {
        fun newInstance(): DiaryFragment {
            return DiaryFragment()
        }
    }
}