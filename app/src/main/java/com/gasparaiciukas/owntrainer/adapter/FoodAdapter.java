package com.gasparaiciukas.owntrainer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.database.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    // Set up recycler view view holder
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerView;
        private TextView tFoodTitle;
        private TextView tFoodCalories;
        private TextView tFoodCarbs;
        private TextView tFoodFat;
        private TextView tFoodProtein;
        private TextView tFoodQuantity;

        FoodViewHolder(View view) {
            super(view);
            containerView = view.findViewById(R.id.food_row);
            tFoodTitle = view.findViewById(R.id.food_row_text);
            tFoodCalories = view.findViewById(R.id.food_row_calories);
            tFoodQuantity = view.findViewById(R.id.food_row_quantity);
            tFoodCarbs = view.findViewById(R.id.food_row_carbs);
            tFoodFat = view.findViewById(R.id.food_row_fat);
            tFoodProtein = view.findViewById(R.id.food_row_protein);
        }
    }

    public static List<Food> foods = new ArrayList<>();

    public FoodAdapter(List<Food> foodList) {
        foods = foodList;
    }

    public void reload() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_row, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // Get information of each row
        int calories = (int) foods.get(position).getCalories();
        int protein = (int) foods.get(position).getProtein();
        int fat = (int) foods.get(position).getFat();
        int carbs = (int) foods.get(position).getCarbs();
        int quantity = (int) foods.get(position).getQuantityInG();
        String title = foods.get(position).getTitle();

        // Display information of each row
        holder.tFoodTitle.setText(title);
        holder.tFoodCalories.setText(String.valueOf(calories));
        holder.tFoodQuantity.setText(String.valueOf(quantity));
        holder.tFoodCarbs.setText(String.valueOf(carbs));
        holder.tFoodProtein.setText(String.valueOf(protein));
        holder.tFoodFat.setText(String.valueOf(fat));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}