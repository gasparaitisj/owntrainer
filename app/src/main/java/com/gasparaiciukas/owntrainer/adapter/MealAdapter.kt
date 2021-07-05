package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.MealRowBinding

class MealAdapter(
    private val meals: List<Meal>,
    private val listener: (meal: Meal, position: Int) -> Unit
) : RecyclerView.Adapter<MealAdapter.NewMealViewHolder>() {

    class NewMealViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MealRowBinding.bind(view)
    }

    fun reload() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_row, parent, false)
        return NewMealViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewMealViewHolder, position: Int) {
        // Get information of each row
        val calories = meals[position].calculateCalories().toInt()
        val title = meals[position].title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.layoutItem.setOnClickListener { listener(meals[position], position) }
    }

    override fun getItemCount(): Int {
        return meals.size
    }
}