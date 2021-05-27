package com.gasparaiciukas.owntrainer.activity

import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.ActivityAddMealToDiaryBinding
import io.realm.Realm

class AddMealToDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMealToDiaryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MealAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var primaryKey: String
    private lateinit var realm: Realm
    //private val portionSizeInput: TextInputEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_meal_to_diary)
        primaryKey = intent.getStringExtra("primaryKey").toString()

        // Get meals from database
        realm = Realm.getDefaultInstance()
        val meals: List<Meal> = realm.where(Meal::class.java).findAll()

        // Set up recycler view
        recyclerView = binding.addMealToDiaryRecyclerView
        adapter = MealAdapter(3, meals, primaryKey)
        layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
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