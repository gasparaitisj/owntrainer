package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.ActivitySelectMealItemBinding
import com.gasparaiciukas.owntrainer.network.FoodApi
import io.realm.Realm

class SelectMealItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectMealItemBinding
    private lateinit var adapter: MealAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var position = 0
    private var quantity = 0
    private lateinit var realm: Realm
    private lateinit var foodItem: FoodApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectMealItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get selected food position
        foodItem = intent.getParcelableExtra("foodItem")!!
        position = intent.getIntExtra("position", 0)
        quantity = intent.getIntExtra("quantity", 0)


        // Get meals from database
        realm = Realm.getDefaultInstance()
        val meals: List<Meal> = realm.where(Meal::class.java).findAll()

        // Set up recycler view
        adapter = MealAdapter(2, foodItem, meals, quantity.toDouble())
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