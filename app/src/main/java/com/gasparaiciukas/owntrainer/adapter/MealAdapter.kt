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
import com.gasparaiciukas.owntrainer.databinding.RowMealBinding

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RowMealBinding.bind(view)

        init {
            this.itemView.isLongClickable = true
        }
    }

    data class MealAdapterFormatStrings(
        val calories: String,
    )

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

    private lateinit var formatStrings: MealAdapterFormatStrings

    fun setOnClickListeners(
        singleClickListener: ((mealWithFoodEntries: MealWithFoodEntries, position: Int) -> Unit)? = null,
        longClickListener: ((mealId: Int, position: Int) -> Unit)? = null
    ) {
        this.singleClickListener = singleClickListener
        this.longClickListener = longClickListener
    }

    fun setFormatStrings(
        formatStrings: MealAdapterFormatStrings
    ) {
        this.formatStrings = formatStrings
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        // Get information of each row
        val calories = items[position].calories.toInt()
        val title = items[position].meal.title

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = String.format(
            formatStrings.calories,
            calories.toString()
        )
        holder.binding.layoutItem.setOnClickListener {
            singleClickListener?.let { click ->
                click(items[position], position)
            }
        }
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

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}