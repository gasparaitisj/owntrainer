package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter.DatabaseFoodViewHolder
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.databinding.FoodRowBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable

class DatabaseFoodAdapter(
    private var items: List<FoodEntry>,
    private val singleClickListener: (food: FoodEntryParcelable) -> Unit,
    private val longClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<DatabaseFoodViewHolder>() {
    class DatabaseFoodViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FoodRowBinding.bind(view)
    }

    class DatabaseFoodItemDiffCallback(
        var oldFoodEntries: List<FoodEntry>,
        var newFoodEntries: List<FoodEntry>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldFoodEntries.size

        override fun getNewListSize() = newFoodEntries.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldFoodEntries[oldItemPosition].mealId == newFoodEntries[newItemPosition].mealId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldFoodEntries[oldItemPosition] == newFoodEntries[newItemPosition]
        }
    }

    fun submitFoodEntries(foodEntries: List<FoodEntry>) {
        val oldList = items
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            DatabaseFoodItemDiffCallback(
                oldList,
                foodEntries
            )
        )

        items = foodEntries
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_row, parent, false)
        return DatabaseFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatabaseFoodViewHolder, position: Int) {
        // Get information of each row
        val calories = items[position].calories.toInt()
        val protein = items[position].protein.toInt()
        val fat = items[position].fat.toInt()
        val carbs = items[position].carbs.toInt()
        val quantity = items[position].quantityInG.toInt()
        val title = items[position].title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.tvQuantity.text = quantity.toString()
        holder.binding.tvCarbs.text = carbs.toString()
        holder.binding.tvProtein.text = protein.toString()
        holder.binding.tvFat.text = fat.toString()
        holder.binding.layoutItem.setOnClickListener {
            singleClickListener(
                FoodEntryParcelable(
                    calories = calories.toDouble(),
                    protein = protein.toDouble(),
                    fat = fat.toDouble(),
                    carbs = carbs.toDouble(),
                    quantityInG = quantity.toDouble(),
                    title = title
            ))
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
        return items.size
    }
}