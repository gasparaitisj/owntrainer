package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter.FoodApiViewHolder
import com.gasparaiciukas.owntrainer.databinding.FoodapiRowBinding
import com.gasparaiciukas.owntrainer.network.FoodApi

class FoodApiAdapter(
    private var foods: List<FoodApi>,
    private val listener: (food: FoodApi, position: Int) -> Unit
) : RecyclerView.Adapter<FoodApiViewHolder>() {

    class FoodApiViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FoodapiRowBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodApiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.foodapi_row, parent, false)
        return FoodApiViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodApiViewHolder, position: Int) {
        // Get information of each row
        val calories = foods[position].nutrients.calories.toInt()
        val label = foods[position].label

        // Display information of each row
        holder.binding.tvTitle.text = label
        holder.binding.tvCalories.text = calories.toString()

        holder.binding.layoutItem.setOnClickListener { listener(foods[position], position) }
    }

    override fun getItemCount(): Int {
        return foods.size
    }
}