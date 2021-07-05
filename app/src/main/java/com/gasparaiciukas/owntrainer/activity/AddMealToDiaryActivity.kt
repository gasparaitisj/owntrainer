package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.ActivityAddMealToDiaryBinding
import io.realm.Realm

class AddMealToDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMealToDiaryBinding
    private lateinit var adapter: MealAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var primaryKey: String
    private lateinit var realm: Realm
    private val listener: (meal: Meal, position: Int) -> Unit = { meal: Meal, position: Int ->
        val realm = Realm.getDefaultInstance()
        val diaryEntry = realm.where(DiaryEntry::class.java)
            .equalTo("yearAndDayOfYear", primaryKey)
            .findFirst()
        if (diaryEntry != null) {
            val mealList = diaryEntry.meals
            realm.executeTransaction {
                mealList.add(meal)
                diaryEntry.meals = mealList
            }
        }
        realm.close()
        finish()
    }
    //private val portionSizeInput: TextInputEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealToDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        primaryKey = intent.getStringExtra("primaryKey").toString()

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