package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.MealRowBinding

class MealAdapter(
    private val meals: List<Meal>,
    private val singleClickListener: (meal: Meal, position: Int) -> Unit,
    private val longClickListener: (position: Int) -> Unit
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
        val calories = meals[position].calculateCalories().toInt()
        val title = meals[position].title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.layoutItem.setOnClickListener { singleClickListener(meals[position], position) }
        holder.binding.layoutItem.setOnLongClickListener {
            val popup = PopupMenu(holder.binding.layoutItem.context, holder.binding.layoutItem)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.meal_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener { longClickListener(position); true }
            true
        }
    }

    override fun getItemCount(): Int {
        return meals.size
    }
}