package com.gasparaiciukas.owntrainer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.activity.MainActivity;
import com.gasparaiciukas.owntrainer.activity.MealItemActivity;
import com.gasparaiciukas.owntrainer.database.DiaryEntry;
import com.gasparaiciukas.owntrainer.database.Food;
import com.gasparaiciukas.owntrainer.database.Meal;
import com.gasparaiciukas.owntrainer.network.FoodApi;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

import static com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter.foodsApi;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    // Set up recycler view view holder
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerView;
        private TextView tMealTitle;
        private TextView tMealCalories;

        MealViewHolder(View view) {
            super(view);
            containerView = view.findViewById(R.id.meal_row);
            tMealTitle = view.findViewById(R.id.meal_row_text);
            tMealCalories = view.findViewById(R.id.meal_row_calories);
        }
    }

    public static List<Meal> meals = new ArrayList<>();
    private Context activityContext;
    private int mAdapterType = 1; // default
    private int mfoodPosition;
    private String mDiaryPrimaryKey;
    private double mQuantity;
    private int mPortionSize;

    // Type 1 constructor (transfer to meal item activity)
    public MealAdapter(List<Meal> mealList) {
        meals = mealList;
    }

    // Type 2 constructor (add selected food to selected meal)
    public MealAdapter(int adapterType, int foodPosition, List<Meal> mealList, double quantity) {
        mAdapterType = adapterType;
        mfoodPosition = foodPosition;
        meals = mealList;
        mQuantity = quantity;
    }

    // Type 3 constructor (add meal to diary)
    public MealAdapter(int adapterType, List<Meal> mealList, String diaryPrimaryKey) {
        mAdapterType = adapterType;
        meals = mealList;
        mDiaryPrimaryKey = diaryPrimaryKey;
    }

    // Type 4 constructor (only view)
    public MealAdapter(int adapterType, List<Meal> mealList) {
        mAdapterType = adapterType;
        meals = mealList;
    }

    public void reload() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_row, parent, false);
        activityContext = parent.getContext();
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        // Get information of each row
        int calories = (int) meals.get(position).calculateCalories();
        String title = meals.get(position).getTitle();

        // Display information of each row
        holder.tMealTitle.setText(title);
        holder.tMealCalories.setText(String.valueOf(calories));

        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If not selectable, start new activity on row clicked
                if (mAdapterType == 1) {
                    Intent intent = new Intent(v.getContext(), MealItemActivity.class);
                    intent.putExtra("position", position);
                    activityContext.startActivity(intent);
                }
                // If selectable, add selected food to selected meal
                else if (mAdapterType == 2) {
                    RealmList<Food> foodList = meals.get(position).getFoodList();
                    Food food = new Food();
                    FoodApi foodApi = foodsApi.get(mfoodPosition);
                    food.setTitle(foodApi.getLabel());
                    food.setCaloriesPer100G(foodApi.getNutrients().getCalories());
                    food.setCarbsPer100G(foodApi.getNutrients().getCarbs());
                    food.setFatPer100G(foodApi.getNutrients().getFat());
                    food.setProteinPer100G(foodApi.getNutrients().getProtein());
                    food.setQuantityInG(mQuantity);
                    food.setCalories(food.calculateCalories(food.getCaloriesPer100G(), food.getQuantityInG()));
                    food.setCarbs(food.calculateCarbs(food.getCarbsPer100G(), food.getQuantityInG()));
                    food.setFat(food.calculateFat(food.getFatPer100G(), food.getQuantityInG()));
                    food.setProtein(food.calculateProtein(food.getProteinPer100G(), food.getQuantityInG()));

                    // Write to database
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NotNull Realm realm) {
                            foodList.add(food);
                            meals.get(position).setFoodList(foodList);
                        }
                    });
                    realm.close();

                    // Finish activity
                    ((Activity) activityContext).finish();
                }
                // If added meal to diary
                else if (mAdapterType == 3) {
                    // Write to database
                    Realm realm = Realm.getDefaultInstance();
                    DiaryEntry diaryEntry = realm.where(DiaryEntry.class)
                            .equalTo("yearAndDayOfYear", mDiaryPrimaryKey)
                            .findFirst();
                    RealmList<Meal> mealList = diaryEntry.getMeals();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NotNull Realm realm) {
                            mealList.add(meals.get(position));
                            diaryEntry.setMeals(mealList);
                        }
                    });
                    realm.close();

                    // Finish activity
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    activityContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}