package com.gasparaiciukas.owntrainer.adapter;

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
import com.gasparaiciukas.owntrainer.activity.FoodItemActivity;
import com.gasparaiciukas.owntrainer.network.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    // Set up recycler view view holder
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerView;
        private TextView tFoodLabel;
        private TextView tFoodCalories;

        FoodViewHolder(View view) {
            super(view);
            containerView = view.findViewById(R.id.food_row);
            tFoodLabel = view.findViewById(R.id.food_row_text);
            tFoodCalories = view.findViewById(R.id.food_row_calories);
        }
    }

    public static List<Food> foods = new ArrayList<>();
    private Context viewContext;
    private Context activityContext;

    public void reload() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_row, parent, false);
        viewContext = view.getContext();
        activityContext = parent.getContext();
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        int calories = (int) foods.get(position).getNutrients().getENERCKCAL();
        String label = foods.get(position).getLabel();
        holder.tFoodLabel.setText(label);
        holder.tFoodCalories.setText(String.valueOf(calories));
        holder.containerView.setTag(position);
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewContext, FoodItemActivity.class);
                intent.putExtra("position", position);
                activityContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}