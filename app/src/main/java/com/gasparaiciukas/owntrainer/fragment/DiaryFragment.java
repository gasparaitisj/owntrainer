package com.gasparaiciukas.owntrainer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.activity.AddMealToDiaryActivity;
import com.gasparaiciukas.owntrainer.activity.FoodItemActivity;
import com.gasparaiciukas.owntrainer.adapter.FoodAdapter;
import com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter;
import com.gasparaiciukas.owntrainer.adapter.MealAdapter;
import com.gasparaiciukas.owntrainer.database.DiaryEntry;
import com.gasparaiciukas.owntrainer.database.Meal;
import com.gasparaiciukas.owntrainer.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

public class DiaryFragment extends Fragment {
    public static DiaryFragment newInstance() {
        return new DiaryFragment();
    }

    // Navigation UI
    private ImageButton backButton;
    private ImageButton forwardButton;
    private TextView tDayOfWeek;
    private TextView tMonthOfYear;
    private TextView tDayOfMonth;

    // Nutrition UI
    private TextView tDailyCaloriesPercentage;
    private TextView tDailyCaloriesConsumed;
    private TextView tDailyCaloriesIntake;
    private TextView tDailyProteinPercentage;
    private TextView tDailyProteinConsumed;
    private TextView tDailyProteinIntake;
    private TextView tDailyFatPercentage;
    private TextView tDailyFatConsumed;
    private TextView tDailyFatIntake;
    private TextView tDailyCarbsPercentage;
    private TextView tDailyCarbsConsumed;
    private TextView tDailyCarbsIntake;

    // Recycler view
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // FAB
    private FloatingActionButton fab;

    // Database
    private Realm realm;
    private DiaryEntry diaryEntry;
    private User user;
    private LocalDate currentDay;

    // Fragment
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = requireActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary, container, false);
        // Get diary entry
        getEntry();

        // Init UI
        initUi(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.reload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void getEntry() {
        // Get user from database
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        // Get day to be showed from preferences
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("diary", Context.MODE_PRIVATE);
        int year = sharedPreferences.getInt("year", 0);
        int month = sharedPreferences.getInt("month", 0);
        int day = sharedPreferences.getInt("day", 0);
        currentDay = LocalDate.of(year, month, day);

        // Get diary entry from database
        diaryEntry = realm.where(DiaryEntry.class)
                .equalTo("yearAndDayOfYear",
                        String.valueOf(currentDay.getYear())
                                + currentDay.getDayOfYear())
                .findFirst();

        // If current day's entry does not exist, insert it to the database
        if (diaryEntry == null) {
            diaryEntry = new DiaryEntry();
            diaryEntry.setYearAndDayOfYear(String.valueOf(currentDay.getYear())
                    + currentDay.getDayOfYear());
            diaryEntry.setYear(currentDay.getYear());
            diaryEntry.setDayOfYear(currentDay.getDayOfYear());
            diaryEntry.setDayOfMonth(currentDay.getDayOfMonth());
            diaryEntry.setDayOfWeek(currentDay.getDayOfWeek().getValue());
            diaryEntry.setMonthOfYear(currentDay.getMonthValue());
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NotNull Realm realm) {
                    realm.insertOrUpdate(diaryEntry);
                }
            });
        }
    }

    private void initUi(View rootView) {
        // Navigation UI
        backButton = rootView.findViewById(R.id.diary_back_button);
        forwardButton = rootView.findViewById(R.id.diary_forward_button);
        tDayOfWeek = rootView.findViewById(R.id.diary_current_day_of_week);
        tMonthOfYear = rootView.findViewById(R.id.diary_current_month_of_year);
        tDayOfMonth = rootView.findViewById(R.id.diary_current_day_of_month);

        // Nutrition UI
        tDailyCaloriesPercentage = rootView.findViewById(R.id.diary_daily_goal_calories_percentage);
        tDailyCaloriesConsumed = rootView.findViewById(R.id.diary_daily_goal_calories_consumed);
        tDailyCaloriesIntake = rootView.findViewById(R.id.diary_daily_goal_calories_intake);
        tDailyProteinPercentage = rootView.findViewById(R.id.diary_daily_goal_protein_percentage);
        tDailyProteinConsumed = rootView.findViewById(R.id.diary_daily_goal_protein_consumed);
        tDailyProteinIntake = rootView.findViewById(R.id.diary_daily_goal_protein_intake);
        tDailyFatPercentage = rootView.findViewById(R.id.diary_daily_goal_fat_percentage);
        tDailyFatConsumed = rootView.findViewById(R.id.diary_daily_goal_fat_consumed);
        tDailyFatIntake = rootView.findViewById(R.id.diary_daily_goal_fat_intake);
        tDailyCarbsPercentage = rootView.findViewById(R.id.diary_daily_goal_carbs_percentage);
        tDailyCarbsConsumed = rootView.findViewById(R.id.diary_daily_goal_carbs_consumed);
        tDailyCarbsIntake = rootView.findViewById(R.id.diary_daily_goal_carbs_intake);

        // Navigation
        tDayOfWeek.setText(dayOfWeekToString(diaryEntry.getDayOfWeek()));
        tMonthOfYear.setText(monthOfYearToString(diaryEntry.getMonthOfYear()));
        tDayOfMonth.setText(String.valueOf(diaryEntry.getDayOfMonth()));

        // Intakes
        double dailyKcalIntake = user.getDailyKcalIntake();
        double dailyProteinIntakeInG = user.getDailyProteinIntakeInG();
        double dailyFatIntakeInG = user.getDailyFatIntakeInG();
        double dailyCarbsIntakeInG = user.getDailyCarbsIntakeInG();
        tDailyCaloriesIntake.setText(String.valueOf((int) dailyKcalIntake));
        tDailyProteinIntake.setText(String.valueOf((int) dailyProteinIntakeInG));
        tDailyFatIntake.setText(String.valueOf((int) dailyFatIntakeInG));
        tDailyCarbsIntake.setText(String.valueOf((int) dailyCarbsIntakeInG));

        // Total calories of day
        double caloriesConsumed = diaryEntry.calculateTotalCalories(diaryEntry.getMeals());
        double proteinConsumed = diaryEntry.calculateTotalProtein(diaryEntry.getMeals());
        double fatConsumed = diaryEntry.calculateTotalFat(diaryEntry.getMeals());
        double carbsConsumed = diaryEntry.calculateTotalCarbs(diaryEntry.getMeals());
        tDailyCaloriesConsumed.setText(String.valueOf((int) caloriesConsumed));
        tDailyProteinConsumed.setText(String.valueOf((int) proteinConsumed));
        tDailyFatConsumed.setText(String.valueOf((int) fatConsumed));
        tDailyCarbsConsumed.setText(String.valueOf((int) carbsConsumed));

        // Percentage of daily intake
        double caloriesPercentage = (caloriesConsumed / dailyKcalIntake) * 100;
        double proteinPercentage = (proteinConsumed / dailyProteinIntakeInG) * 100;
        double fatPercentage = (fatConsumed / dailyFatIntakeInG) * 100;
        double carbsPercentage = (carbsConsumed / dailyCarbsIntakeInG) * 100;
        tDailyCaloriesPercentage.setText(String.valueOf((int) caloriesPercentage));
        tDailyProteinPercentage.setText(String.valueOf((int) proteinPercentage));
        tDailyFatPercentage.setText(String.valueOf((int) fatPercentage));
        tDailyCarbsPercentage.setText(String.valueOf((int) carbsPercentage));

        // Add meal to diary on FAB clicked
        fab = rootView.findViewById(R.id.diary_fab_add_meal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), AddMealToDiaryActivity.class);
                intent.putExtra("primaryKey", diaryEntry.getYearAndDayOfYear());
                requireContext().startActivity(intent);
            }
        });

        // Navigation (back button)
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save previous day to preferences
                SharedPreferences sharedPreferences = requireActivity()
                        .getSharedPreferences("diary", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                LocalDate previousDay = currentDay.minusDays(1); // subtract 1 day from current day
                editor.putInt("year", previousDay.getYear());
                editor.putInt("month", previousDay.getMonthValue());
                editor.putInt("day", previousDay.getDayOfMonth());
                editor.apply();

                // Refresh fragment and show previous day
                Fragment f = fragmentManager.findFragmentByTag("DIARY_FRAGMENT");
                final FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.detach(f);
                ft.attach(f);
                ft.commit();
            }
        });

        // Navigation (back button)
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save next day to preferences
                SharedPreferences sharedPreferences = requireActivity()
                        .getSharedPreferences("diary", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                LocalDate nextDay = currentDay.plusDays(1);
                editor.putInt("year", nextDay.getYear());
                editor.putInt("month", nextDay.getMonthValue());
                editor.putInt("day", nextDay.getDayOfMonth());
                editor.apply();

                // Refresh fragment and show next day
                Fragment f = fragmentManager.findFragmentByTag("DIARY_FRAGMENT");
                final FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.detach(f);
                ft.attach(f);
                ft.commit();
            }
        });
    }

    private void initRecyclerView(View view) {
        // Set up recycler view
        recyclerView = view.findViewById(R.id.diary_meal_recycler_view);
        adapter = new MealAdapter(4, diaryEntry.getMeals());
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private String dayOfWeekToString(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            case 5: return "Fri";
            case 6: return "Sat";
            case 7: return "Sun";
            default: return "";
        }
    }

    private String monthOfYearToString(int monthOfYear) {
        switch (monthOfYear) {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "";
        }
    }
}