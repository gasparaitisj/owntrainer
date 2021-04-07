package com.gasparaiciukas.owntrainer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.activity.MealItemActivity;
import com.gasparaiciukas.owntrainer.database.Food;
import com.gasparaiciukas.owntrainer.database.Meal;
import com.gasparaiciukas.owntrainer.network.FoodApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    private boolean mIsSelectable = false;
    private int mfoodPosition;
    private double mQuantity;

    // Constructor for selectable food rows
    public MealAdapter(boolean isSelectable, int foodPosition, List<Meal> mealList, double quantity) {
        mIsSelectable = isSelectable;
        mfoodPosition = foodPosition;
        meals = mealList;
        mQuantity = quantity;
    }

    // Constructor for non-selectable food rows
    public MealAdapter(List<Meal> mealList) {
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
                if (!mIsSelectable) {
                    Intent intent = new Intent(v.getContext(), MealItemActivity.class);
                    intent.putExtra("position", position);
                    activityContext.startActivity(intent);
                }
                // If selectable, add selected food to selected meal
                else {
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}