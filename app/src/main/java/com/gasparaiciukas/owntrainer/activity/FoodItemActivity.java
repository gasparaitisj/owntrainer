package com.gasparaiciukas.owntrainer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gasparaiciukas.owntrainer.database.User;
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter;
import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.network.Nutrients;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

import static com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter.foodsApi;

public class FoodItemActivity extends AppCompatActivity {

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

    // Pie chart
    private PieChart pieChart;
    private PieDataSet pieDataSet;
    private PieData pieData;

    // Text views
    private TextView tFoodTitle;
    private TextView tCarbsWeight;
    private TextView tCarbsDailyIntake;
    private TextView tFatWeight;
    private TextView tFatDailyIntake;
    private TextView tProteinWeight;
    private TextView tProteinDailyIntake;
    private TextView tCalorieCount;
    private TextView tCalorieDailyIntake;
    private Button addToMealButton;
    private TextInputEditText inputFoodWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item);
        getViews();
        getData();
        initUi();
    }

    private void getViews() {
        pieChart = findViewById(R.id.food_pie_chart);
        tCarbsWeight = findViewById(R.id.food_carbs_weight);
        tCarbsDailyIntake = findViewById(R.id.food_carbs_percentage);
        tFatWeight = findViewById(R.id.food_fat_weight);
        tFatDailyIntake = findViewById(R.id.food_fat_percentage);
        tProteinWeight = findViewById(R.id.food_protein_weight);
        tProteinDailyIntake = findViewById(R.id.food_protein_percentage);
        tCalorieCount = findViewById(R.id.food_calories_count);
        tCalorieDailyIntake = findViewById(R.id.food_calories_percentage);
        tFoodTitle = findViewById(R.id.food_item_title);
        addToMealButton = findViewById(R.id.food_add_to_meal_button);
        inputFoodWeight = findViewById(R.id.food_weight_input_text);
    }

    private void getData() {
        position = getIntent().getIntExtra("position", 0); // get clicked item position

        // Set up button
        addToMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SelectMealItemActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("quantity", Integer.parseInt(inputFoodWeight.getText().toString()));
                startActivity(intent);
            }
        });

        // Get nutrients from food item
        Nutrients nutrients = foodsApi.get(position).getNutrients();
        carbs = (float) nutrients.getCarbs();
        calories = (float) nutrients.getCalories();
        fat = (float) nutrients.getFat();
        protein = (float) nutrients.getProtein();

        // Calculate percentage of each item
        float sum = carbs + fat + protein;
        carbsPercentage = (carbs / sum) * 100;
        fatPercentage = (fat / sum) * 100;
        proteinPercentage = (protein / sum) * 100;

        // Get daily intake percentages
        Realm realm = Realm.getDefaultInstance();
        User u = realm.where(User.class)
                .equalTo("userId", "user")
                .findFirst();
        calorieDailyIntake = (float) u.getDailyKcalIntake();
        carbsDailyIntake = (float) u.getDailyCarbsIntakeInG();
        fatDailyIntake = (float) u.getDailyFatIntakeInG();
        proteinDailyIntake = (float) u.getDailyProteinIntakeInG();
        realm.close();
    }

    private void initUi() {
        // Set up text views
        tFoodTitle.setText(foodsApi.get(position).getLabel());
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
}