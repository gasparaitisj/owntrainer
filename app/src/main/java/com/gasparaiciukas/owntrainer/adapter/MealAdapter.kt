package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.MealRowBinding

class MealAdapter(
    val mealsWithFoodEntries: MutableList<MealWithFoodEntries>,
    private val singleClickListener: (mealWithFoodEntries: MealWithFoodEntries, position: Int) -> Unit,
    private val longClickListener: (mealId: Int, position: Int) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MealRowBinding.bind(view)
        init {
            this.itemView.isLongClickable = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_row, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        // Get information of each row
        val calories = mealsWithFoodEntries[position].calculateCalories().toInt()
        val title = mealsWithFoodEntries[position].meal.title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.layoutItem.setOnClickListener { singleClickListener(mealsWithFoodEntries[position], position) }
        holder.binding.layoutItem.setOnLongClickListener {
            val popup = PopupMenu(holder.binding.layoutItem.context, holder.binding.layoutItem)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.meal_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener { longClickListener(mealsWithFoodEntries[position].meal.mealId, position); true }
            true
        }
    }

    override fun getItemCount(): Int {
        return mealsWithFoodEntries.size
    }
}