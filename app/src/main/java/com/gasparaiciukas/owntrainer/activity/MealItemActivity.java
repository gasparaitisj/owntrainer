package com.gasparaiciukas.owntrainer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.adapter.FoodAdapter;
import com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter;
import com.gasparaiciukas.owntrainer.database.Food;
import com.gasparaiciukas.owntrainer.database.Meal;
import com.gasparaiciukas.owntrainer.database.User;
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MealItemActivity extends AppCompatActivity {

    // Pie chart
    private PieChart pieChart;
    private PieDataSet pieDataSet;
    private PieData pieData;

    // Data
    private int position;
    private float carbs;
    private float carbsPercentage;
    private float carbsDailyIntake;
    private float calories;
    private float calorieDailyIntake;
    private float fat;
    private float fatPercentage;
    private float fatDailyIntake;
    private float protein;
    private float proteinPercentage;
    private float proteinDailyIntake;
    private int quantity;

    // Text views
    private TextView tMealTitle;
    private TextView tMealInstructions;
    private TextView tCarbsWeight;
    private TextView tCarbsDailyIntake;
    private TextView tFatWeight;
    private TextView tFatDailyIntake;
    private TextView tProteinWeight;
    private TextView tProteinDailyIntake;
    private TextView tCalorieCount;
    private TextView tCalorieDailyIntake;

    // Recycler view
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_item);

        // Get selected food position
        position = getIntent().getIntExtra("position", 0);

        // Views
        pieChart = findViewById(R.id.meal_item_pie_chart);
        tCarbsWeight = findViewById(R.id.meal_item_carbs_weight);
        tCarbsDailyIntake = findViewById(R.id.meal_item_carbs_percentage);
        tFatWeight = findViewById(R.id.meal_item_fat_weight);
        tFatDailyIntake = findViewById(R.id.meal_item_fat_percentage);
        tProteinWeight = findViewById(R.id.meal_item_protein_weight);
        tProteinDailyIntake = findViewById(R.id.meal_item_protein_percentage);
        tCalorieCount = findViewById(R.id.meal_item_calories_count);
        tCalorieDailyIntake = findViewById(R.id.meal_item_calories_percentage);
        tMealTitle = findViewById(R.id.meal_item_title);
        tMealInstructions = findViewById(R.id.meal_item_instructions);
        tMealInstructions.setMovementMethod(new ScrollingMovementMethod());
        recyclerView = findViewById(R.id.meal_item_recycler_view);

        // Recycler view
        realm = Realm.getDefaultInstance();
        List<Meal> meals = realm.where(Meal.class).findAll();
        List<Food> foodList = meals.get(position).getFoodList();
        realm.close();
        adapter = new FoodAdapter(foodList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);


        // Get nutrients from food item
        carbs = (float) meals.get(position).calculateCarbs();
        calories = (float) meals.get(position).calculateCalories();
        fat = (float) meals.get(position).calculateFat();
        protein = (float) meals.get(position).calculateProtein();

        // Calculate percentage of each item
        float sum = carbs + fat + protein;
        carbsPercentage = (carbs / sum) * 100;
        fatPercentage = (fat / sum) * 100;
        proteinPercentage = (protein / sum) * 100;

        // Get daily intake percentages
        realm = Realm.getDefaultInstance();
        User u = realm.where(User.class)
                .equalTo("userId", "user")
                .findFirst();
        calorieDailyIntake = (float) u.getDailyKcalIntake();
        carbsDailyIntake = (float) u.getDailyCarbsIntakeInG();
        fatDailyIntake = (float) u.getDailyFatIntakeInG();
        proteinDailyIntake = (float) u.getDailyProteinIntakeInG();
        realm.close();

        // Set up text views
        tMealTitle.setText(meals.get(position).getTitle());
        tMealInstructions.setText(meals.get(position).getInstructions());
        tCarbsWeight.setText(String.valueOf((int) carbs));
        tCarbsDailyIntake.setText(String.format("%s %%", (int) ((carbs / carbsDailyIntake) * 100)));
        tFatWeight.setText(String.valueOf((int) fat));
        tFatDailyIntake.setText(String.format("%s %%", (int) ((fat / fatDailyIntake) * 100)));
        tProteinWeight.setText(String.valueOf((int) protein));
        tProteinDailyIntake.setText(String.format("%s %%", (int) ((protein / proteinDailyIntake) * 100)));
        tCalorieCount.setText(String.valueOf((int) calories));
        tCalorieDailyIntake.setText(String.format("%s %%", (int) ((calories / calorieDailyIntake) * 100)));

        // Create colors representing nutrients
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.colorGold)); // carbs
        colors.add(ContextCompat.getColor(this, R.color.colorOrange)); // fat
        colors.add(ContextCompat.getColor(this, R.color.colorSmokeDark)); // protein

        // Add data to pie chart
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(carbsPercentage, "Carbohydrates"));
        entries.add(new PieEntry(fatPercentage, "Fat"));
        entries.add(new PieEntry(proteinPercentage, "Protein"));
        pieDataSet = new PieDataSet(entries, "Data");

        // Add style to pie chart
        pieDataSet.setColors(colors); // chart colors
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new NutrientValueFormatter()); // adjust labels
        pieData.setValueTextSize(12);
        pieChart.setData(pieData);
        pieChart.setCenterText((int) calories + "\nkCal"); // calorie text inside inner circle
        pieChart.setCenterTextSize(14);
        pieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        pieChart.setCenterTextRadiusPercent(100);
        pieChart.setHoleColor(ContextCompat.getColor(this, R.color.colorRed));
        pieChart.setHoleRadius(30);
        pieChart.setTransparentCircleRadius(0);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(false);
        pieChart.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.reload();
    }
}