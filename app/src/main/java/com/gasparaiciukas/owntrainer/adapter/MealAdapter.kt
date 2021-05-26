package com.gasparaiciukas.owntrainer.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.activity.MainActivity
import com.gasparaiciukas.owntrainer.activity.MealItemActivity
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.Food
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.network.FoodApi
import io.realm.Realm
import java.util.*

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private lateinit var activityContext: Context
    private var mAdapterType = 1 // default
    private lateinit var mFoodApi: FoodApi
    private lateinit var mDiaryPrimaryKey: String
    private var mQuantity = 0.0
    private val mPortionSize = 0

    class MealViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val containerView: LinearLayout = view.findViewById(R.id.meal_row)
        val tMealTitle: TextView = view.findViewById(R.id.meal_row_text)
        val tMealCalories: TextView = view.findViewById(R.id.meal_row_calories)
    }

    // Type 1 constructor (transfer to meal item activity)
    constructor(mealList: List<Meal>) {
        meals = mealList
    }

    // Type 2 constructor (add selected food to selected meal)
    constructor(adapterType: Int, foodApi: FoodApi, mealList: List<Meal>, quantity: Double) {
        mAdapterType = adapterType
        mFoodApi = foodApi
        meals = mealList
        mQuantity = quantity
    }

    // Type 3 constructor (add meal to diary)
    constructor(adapterType: Int, mealList: List<Meal>, diaryPrimaryKey: String?) {
        mAdapterType = adapterType
        meals = mealList
        mDiaryPrimaryKey = diaryPrimaryKey.toString()
    }

    // Type 4 constructor (only view)
    constructor(adapterType: Int, mealList: List<Meal>) {
        mAdapterType = adapterType
        meals = mealList
    }

    fun reload() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_row, parent, false)
        activityContext = parent.context
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        // Get information of each row
        val calories = meals[position].calculateCalories().toInt()
        val title = meals[position].title

        // Display information of each row
        holder.tMealTitle.text = title
        holder.tMealCalories.text = calories.toString()
        holder.containerView.setOnClickListener { v ->
            // If not selectable, start new activity on row clicked
            when (mAdapterType) {
                1 -> {
                    val intent = Intent(v.context, MealItemActivity::class.java)
                    intent.putExtra("position", position)
                    activityContext.startActivity(intent)
                }
                2 -> {
                    val foodList = meals[position].foodList
                    val food = Food()
                    food.title = mFoodApi.label
                    food.caloriesPer100G = mFoodApi.nutrients.calories
                    food.carbsPer100G = mFoodApi.nutrients.carbs
                    food.fatPer100G = mFoodApi.nutrients.fat
                    food.proteinPer100G = mFoodApi.nutrients.protein
                    food.quantityInG = mQuantity
                    food.calories = food.calculateCalories(food.caloriesPer100G, food.quantityInG)
                    food.carbs = food.calculateCarbs(food.carbsPer100G, food.quantityInG)
                    food.fat = food.calculateFat(food.fatPer100G, food.quantityInG)
                    food.protein = food.calculateProtein(food.proteinPer100G, food.quantityInG)

                    // Write to database
                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        foodList.add(food)
                        meals[position].foodList = foodList
                    }
                    realm.close()

                    // Finish activity
                    (activityContext as Activity).finish()
                }
                3 -> {
                    // Write to database
                    val realm = Realm.getDefaultInstance()
                    val diaryEntry = realm.where(DiaryEntry::class.java)
                        .equalTo("yearAndDayOfYear", mDiaryPrimaryKey)
                        .findFirst()
                    if (diaryEntry != null) {
                        val mealList = diaryEntry.meals
                        realm.executeTransaction {
                            mealList.add(meals[position])
                            diaryEntry.meals = mealList
                        }
                    }
                    realm.close()

                    // Finish activity
                    val intent = Intent(v.context, MainActivity::class.java)
                    activityContext.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return meals.size
    }

    companion object {
        var meals: List<Meal> = ArrayList()
    }
}