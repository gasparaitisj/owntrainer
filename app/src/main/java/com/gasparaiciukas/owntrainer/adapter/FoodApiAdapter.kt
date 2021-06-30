package com.gasparaiciukas.owntrainer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.activity.FoodItemActivity
import com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter.FoodApiViewHolder
import com.gasparaiciukas.owntrainer.databinding.FoodapiRowBinding
import com.gasparaiciukas.owntrainer.network.FoodApi

class FoodApiAdapter(foods: List<FoodApi>) : RecyclerView.Adapter<FoodApiViewHolder>() {
    private lateinit var viewContext: Context
    private lateinit var activityContext: Context
    private var foodsApi = foods

    class FoodApiViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FoodapiRowBinding.bind(view)
    }
    fun reload(foods: List<FoodApi>) {
        foodsApi = foods
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodApiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.foodapi_row, parent, false)
        viewContext = view.context
        activityContext = parent.context
        return FoodApiViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodApiViewHolder, position: Int) {
        // Get information of each row
        val calories = foodsApi[position].nutrients.calories.toInt()
        val label = foodsApi[position].label

        // Display information of each row
        holder.binding.tvTitle.text = label
        holder.binding.tvCalories.text = calories.toString()

        // Start new activity on row clicked
        holder.binding.layoutItem.setOnClickListener {
            val intent = Intent(viewContext, FoodItemActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("foodItem", foodsApi[position])
            activityContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return foodsApi.size
    }
}