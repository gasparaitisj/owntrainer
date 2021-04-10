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
import com.gasparaiciukas.owntrainer.network.FoodApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodApiAdapter extends RecyclerView.Adapter<FoodApiAdapter.FoodApiViewHolder> {

    // Set up recycler view view holder
    public static class FoodApiViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerView;
        private TextView tFoodLabel;
        private TextView tFoodCalories;

        FoodApiViewHolder(View view) {
            super(view);
            containerView = view.findViewById(R.id.foodapi_row);
            tFoodLabel = view.findViewById(R.id.foodapi_row_text);
            tFoodCalories = view.findViewById(R.id.foodapi_row_calories);
        }
    }

    public static List<FoodApi> foodsApi = new ArrayList<>();
    private Context viewContext;
    private Context activityContext;

    public void reload() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodApiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodapi_row, parent, false);
        viewContext = view.getContext();
        activityContext = parent.getContext();
        return new FoodApiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodApiViewHolder holder, int position) {
        // Get information of each row
        int calories = (int) foodsApi.get(position).getNutrients().getCalories();
        String label = foodsApi.get(position).getLabel();

        // Display information of each row
        holder.tFoodLabel.setText(label);
        holder.tFoodCalories.setText(String.valueOf(calories));

        // Start new activity on row clicked
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
        return foodsApi.size();
    }
}