package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.databinding.MealRowBinding
import timber.log.Timber
import javax.inject.Inject

class MealAdapter @Inject constructor(
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MealRowBinding.bind(view)

        init {
            this.itemView.isLongClickable = true
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<MealWithFoodEntries>() {
        override fun areItemsTheSame(
            oldItem: MealWithFoodEntries,
            newItem: MealWithFoodEntries
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MealWithFoodEntries,
            newItem: MealWithFoodEntries
        ): Boolean {
            return oldItem.meal.mealId == newItem.meal.mealId
        }
    }

    private var singleClickListener: ((MealWithFoodEntries, Int) -> Unit)? = null
    private var longClickListener: ((Int, Int) -> Unit)? = null
    private val differ = AsyncListDiffer(this, diffCallback)

    var items: List<MealWithFoodEntries>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    fun setOnClickListeners(
        singleClickListener: ((mealWithFoodEntries: MealWithFoodEntries, position: Int) -> Unit)?,
        longClickListener: ((mealId: Int, position: Int) -> Unit)?
    ) {
        this.singleClickListener = singleClickListener
        this.longClickListener = longClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_row, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        // Get information of each row
        val calories = items[position].calories.toInt()
        val title = items[position].meal.title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        if (singleClickListener != null) {
            Timber.d("listener is set")
            holder.binding.layoutItem.setOnClickListener {
                singleClickListener?.let { click ->
                    click(items[position], position)
                }
            }
        }
        if (longClickListener != null) {
            holder.binding.layoutItem.setOnLongClickListener {
                val popup = PopupMenu(holder.binding.layoutItem.context, holder.binding.layoutItem)
                val inflater = popup.menuInflater
                inflater.inflate(R.menu.meal_menu, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener {
                    longClickListener?.let { click ->
                        click(items[position].meal.mealId, position)
                    }
                    true
                }
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}