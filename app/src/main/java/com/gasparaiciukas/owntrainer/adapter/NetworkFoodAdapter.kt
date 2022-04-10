package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.NetworkFoodAdapter.NetworkFoodViewHolder
import com.gasparaiciukas.owntrainer.databinding.FoodRowBinding
import com.gasparaiciukas.owntrainer.network.Food
import javax.inject.Inject


class NetworkFoodAdapter @Inject constructor(
) : RecyclerView.Adapter<NetworkFoodViewHolder>() {

    class NetworkFoodViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FoodRowBinding.bind(view)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(
            oldItem: Food,
            newItem: Food
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Food,
            newItem: Food
        ): Boolean {
            return oldItem.fdcId == newItem.fdcId
        }
    }

    private var singleClickListener: ((food: Food, position: Int) -> Unit)? = null
    private var longClickListener: (Unit)? = null
    private val differ = AsyncListDiffer(this, diffCallback)

    var items: List<Food>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    fun setOnClickListeners(
        singleClickListener: ((food: Food, position: Int) -> Unit)? = null,
        longClickListener: (Unit)? = null
    ) {
        this.singleClickListener = singleClickListener
        this.longClickListener = longClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_row, parent, false)
        return NetworkFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: NetworkFoodViewHolder, position: Int) {
        // Get information of each row
        val title = items[position].description
        val quantity = 100.0
        var calories = 0.0
        var protein = 0.0
        var fat = 0.0
        var carbs = 0.0

        val nutrients = items[position].foodNutrients
        nutrients?.forEach { nutrient ->
            if (nutrient.nutrientId == 1003) protein = nutrient.value ?: 0.0
            if (nutrient.nutrientId == 1004) fat = nutrient.value ?: 0.0
            if (nutrient.nutrientId == 1005) carbs = nutrient.value ?: 0.0
            if (nutrient.nutrientId == 1008) calories = nutrient.value ?: 0.0
        }

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvCalories.text = calories.toString()
        holder.binding.tvCarbs.text = carbs.toString()
        holder.binding.tvProtein.text = protein.toString()
        holder.binding.tvFat.text = fat.toString()
        holder.binding.tvQuantity.text = quantity.toString()
        holder.binding.layoutItem.setOnClickListener {
            singleClickListener?.let { click ->
                click(items[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}