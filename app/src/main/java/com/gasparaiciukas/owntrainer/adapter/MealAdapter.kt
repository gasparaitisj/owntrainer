package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.MealRowBinding

class MealAdapter(
    private var items: List<MealWithFoodEntries>,
    private val singleClickListener: (mealWithFoodEntries: MealWithFoodEntries, position: Int) -> Unit,
    private val longClickListener: (mealId: Int, position: Int) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MealRowBinding.bind(view)

        init {
            this.itemView.isLongClickable = true
        }
    }

    class MealItemDiffCallback(
        var oldMeals: List<MealWithFoodEntries>,
        var newMeals: List<MealWithFoodEntries>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldMeals.size

        override fun getNewListSize() = newMeals.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldMeals[oldItemPosition].meal.id == newMeals[newItemPosition].meal.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldMeals[oldItemPosition] == newMeals[newItemPosition]
        }
    }

    fun submitMeals(meals: List<MealWithFoodEntries>) {
        val oldList = items
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            MealItemDiffCallback(
                oldList,
                meals
            )
        )

        items = meals
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_row, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        // Get information of each row
        val calories = items[position].calculateCalories().toInt()
        val title = items[position].meal.title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.layoutItem.setOnClickListener {
            singleClickListener(
                items[position],
                position
            )
        }
        holder.binding.layoutItem.setOnLongClickListener {
            val popup = PopupMenu(holder.binding.layoutItem.context, holder.binding.layoutItem)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.meal_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                longClickListener(
                    items[position].meal.id,
                    position
                ); true
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}