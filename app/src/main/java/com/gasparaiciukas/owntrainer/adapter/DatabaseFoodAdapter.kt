package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter.DatabaseFoodViewHolder
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.databinding.FoodRowBinding

class DatabaseFoodAdapter(private val foods: List<FoodEntry>) : RecyclerView.Adapter<DatabaseFoodViewHolder>() {
    class DatabaseFoodViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FoodRowBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_row, parent, false)
        return DatabaseFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatabaseFoodViewHolder, position: Int) {
        // Get information of each row
        val calories = foods[position].calories.toInt()
        val protein = foods[position].protein.toInt()
        val fat = foods[position].fat.toInt()
        val carbs = foods[position].carbs.toInt()
        val quantity = foods[position].quantityInG.toInt()
        val title = foods[position].title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.tvQuantity.text = quantity.toString()
        holder.binding.tvCarbs.text = carbs.toString()
        holder.binding.tvProtein.text = protein.toString()
        holder.binding.tvFat.text = fat.toString()
    }

    override fun getItemCount(): Int {
        return foods.size
    }
}