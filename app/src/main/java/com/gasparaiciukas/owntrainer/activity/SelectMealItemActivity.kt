package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Food
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.ActivitySelectMealItemBinding
import com.gasparaiciukas.owntrainer.network.FoodApi
import io.realm.Realm

class SelectMealItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectMealItemBinding
    private lateinit var adapter: MealAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var position = 0
    private var quantity = 0.0
    private lateinit var realm: Realm
    private lateinit var foodItem: FoodApi
    private val listener: (meal: Meal, position: Int) -> Unit = { meal: Meal, _: Int ->
        val foodList = meal.foodList
        val food = Food()
        food.title = foodItem.label
        food.caloriesPer100G = foodItem.nutrients.calories
        food.carbsPer100G = foodItem.nutrients.carbs
        food.fatPer100G = foodItem.nutrients.fat
        food.proteinPer100G = foodItem.nutrients.protein
        food.quantityInG = quantity
        food.calories = food.calculateCalories(food.caloriesPer100G, food.quantityInG)
        food.carbs = food.calculateCarbs(food.carbsPer100G, food.quantityInG)
        food.fat = food.calculateFat(food.fatPer100G, food.quantityInG)
        food.protein = food.calculateProtein(food.proteinPer100G, food.quantityInG)

        // Write to database
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            foodList.add(food)
            meal.foodList = foodList
        }
        realm.close()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectMealItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get selected food position
        foodItem = intent.getParcelableExtra("foodItem")!!
        position = intent.getIntExtra("position", 0)
        quantity = intent.getIntExtra("quantity", 0).toDouble()


        // Get meals from database
        realm = Realm.getDefaultInstance()
        val meals: List<Meal> = realm.where(Meal::class.java).findAll()

        // Set up recycler view
        adapter = MealAdapter(meals, listener)
        layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()
        adapter.reload()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}