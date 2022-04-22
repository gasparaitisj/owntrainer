package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter.DatabaseFoodViewHolder
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.databinding.RowFoodBinding
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import javax.inject.Inject
import kotlin.math.roundToInt

class DatabaseFoodAdapter @Inject constructor(
) : RecyclerView.Adapter<DatabaseFoodViewHolder>() {

    class DatabaseFoodViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RowFoodBinding.bind(view)

        init {
            this.itemView.isLongClickable = true
        }
    }

    data class DatabaseFoodAdapterFormatStrings(
        val quantity: String,
        val calories: String,
        val protein: String,
        val carbs: String,
        val fat: String,
    )

    private val diffCallback = object : DiffUtil.ItemCallback<FoodEntry>() {
        override fun areItemsTheSame(
            oldItem: FoodEntry,
            newItem: FoodEntry
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FoodEntry,
            newItem: FoodEntry
        ): Boolean {
            return oldItem.foodEntryId == newItem.foodEntryId
        }
    }

    private var singleClickListener: ((FoodEntryParcelable) -> Unit)? = null
    private var longClickListener: ((FoodEntry) -> Unit)? = null
    private val differ = AsyncListDiffer(this, diffCallback)

    var items: List<FoodEntry>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private lateinit var formatStrings: DatabaseFoodAdapterFormatStrings

    fun setOnClickListeners(
        singleClickListener: ((foodEntryParcelable: FoodEntryParcelable) -> Unit)? = null,
        longClickListener: ((foodEntry: FoodEntry) -> Unit)? = null
    ) {
        this.singleClickListener = singleClickListener
        this.longClickListener = longClickListener
    }

    fun setFormatStrings(
        formatStrings: DatabaseFoodAdapterFormatStrings
    ) {
        this.formatStrings = formatStrings
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_food, parent, false)
        return DatabaseFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatabaseFoodViewHolder, position: Int) {
        // Display information of each row
        holder.binding.tvTitle.text = items[position].title
        holder.binding.tvQuantity.text = String.format(
            formatStrings.quantity,
            items[position].quantityInG.roundToInt().toString()
        )
        holder.binding.tvCalories.text = String.format(
            formatStrings.calories,
            items[position].calories.roundToInt().toString()
        )
        holder.binding.tvProtein.text = String.format(
            formatStrings.protein,
            items[position].protein.roundToInt().toString()
        )
        holder.binding.tvCarbs.text = String.format(
            formatStrings.carbs,
            items[position].carbs.roundToInt().toString()
        )
        holder.binding.tvFat.text = String.format(
            formatStrings.fat,
            items[position].fat.roundToInt().toString()
        )
        holder.binding.layoutItem.setOnClickListener {
            singleClickListener?.let { click ->
                click(
                    FoodEntryParcelable(
                        title = items[position].title,
                        caloriesPer100G = items[position].caloriesPer100G,
                        proteinPer100G = items[position].proteinPer100G,
                        fatPer100G = items[position].fatPer100G,
                        carbsPer100G = items[position].carbsPer100G,
                        calories = items[position].calories,
                        protein = items[position].protein,
                        fat = items[position].fat,
                        carbs = items[position].carbs,
                        quantityInG = items[position].quantityInG,
                    )
                )
            }
        }
        holder.binding.layoutItem.setOnLongClickListener {
            val popup = PopupMenu(holder.binding.layoutItem.context, holder.binding.layoutItem)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.food_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                longClickListener?.let { click ->
                    click(
                        items[position]
                    )
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