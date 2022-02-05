package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter.DatabaseFoodViewHolder
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.databinding.FoodRowBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable

class DatabaseFoodAdapter(
    val foods: MutableList<FoodEntry>,
    private val singleClickListener: (food: FoodEntryParcelable) -> Unit,
    private val longClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<DatabaseFoodViewHolder>() {
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
        holder.binding.layoutItem.setOnClickListener {
            val foodEntryParcelable = FoodEntryParcelable()
            foodEntryParcelable.calories = calories.toDouble()
            foodEntryParcelable.protein = protein.toDouble()
            foodEntryParcelable.fat = fat.toDouble()
            foodEntryParcelable.carbs = carbs.toDouble()
            foodEntryParcelable.quantityInG = quantity.toDouble()
            foodEntryParcelable.title = title
            singleClickListener(foodEntryParcelable)
        }
        holder.binding.layoutItem.setOnLongClickListener {
            val popup = PopupMenu(holder.binding.layoutItem.context, holder.binding.layoutItem)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.food_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener { longClickListener(position); true }
            true
        }
    }

    override fun getItemCount(): Int {
        return foods.size
    }
}