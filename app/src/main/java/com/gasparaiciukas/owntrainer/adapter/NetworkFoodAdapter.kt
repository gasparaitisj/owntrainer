package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.NetworkFoodAdapter.NetworkFoodViewHolder
import com.gasparaiciukas.owntrainer.databinding.FoodRowBinding
import com.gasparaiciukas.owntrainer.network.Food

class NetworkFoodAdapter(
    private var foods: List<Food>,
    private val listener: (food: Food, position: Int) -> Unit
) : RecyclerView.Adapter<NetworkFoodViewHolder>() {

    class NetworkFoodViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FoodRowBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_row, parent, false)
        return NetworkFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: NetworkFoodViewHolder, position: Int) {
        // Get information of each row
        val title = foods[position].description
        var calories = 0.0
        var protein = 0.0
        var fat = 0.0
        var carbs = 0.0

        val nutrients = foods[position].foodNutrients
        if (nutrients != null) {
            for (nutrient in nutrients) {
                if (nutrient.nutrientId == 1003) protein = nutrient.value ?: 0.0
                if (nutrient.nutrientId == 1004) fat = nutrient.value ?: 0.0
                if (nutrient.nutrientId == 1005) carbs = nutrient.value ?: 0.0
                if (nutrient.nutrientId == 1008) calories = nutrient.value ?: 0.0
            }
        }

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.tvCarbs.text = carbs.toString()
        holder.binding.tvProtein.text = protein.toString()
        holder.binding.tvFat.text = fat.toString()
        holder.binding.layoutItem.setOnClickListener { listener(foods[position], position) }
    }

    override fun getItemCount(): Int {
        return foods.size
    }
}