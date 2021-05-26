package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.FoodAdapter.FoodViewHolder
import com.gasparaiciukas.owntrainer.database.Food
import java.util.*

class FoodAdapter(foodList: List<Food>) : RecyclerView.Adapter<FoodViewHolder>() {
    private val foods: List<Food> = foodList
    class FoodViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val containerView: LinearLayout = view.findViewById(R.id.food_row)
        val tFoodTitle: TextView = view.findViewById(R.id.food_row_text)
        val tFoodCalories: TextView = view.findViewById(R.id.food_row_calories)
        val tFoodCarbs: TextView = view.findViewById(R.id.food_row_carbs)
        val tFoodFat: TextView = view.findViewById(R.id.food_row_fat)
        val tFoodProtein: TextView = view.findViewById(R.id.food_row_protein)
        val tFoodQuantity: TextView = view.findViewById(R.id.food_row_quantity)
    }

    fun reload() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_row, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        // Get information of each row
        val calories = foods[position].calories.toInt()
        val protein = foods[position].protein.toInt()
        val fat = foods[position].fat.toInt()
        val carbs = foods[position].carbs.toInt()
        val quantity = foods[position].quantityInG.toInt()
        val title = foods[position].title

        // Display information of each row
        holder.tFoodTitle.text = title
        holder.tFoodCalories.text = calories.toString()
        holder.tFoodQuantity.text = quantity.toString()
        holder.tFoodCarbs.text = carbs.toString()
        holder.tFoodProtein.text = protein.toString()
        holder.tFoodFat.text = fat.toString()
    }

    override fun getItemCount(): Int {
        return foods.size
    }
}