package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.ActivityCreateMealItemBinding
import io.realm.Realm

class CreateMealItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateMealItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMealItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        // Back button
        binding.appBar.setNavigationOnClickListener { onBackPressed() }

        // Save button
        binding.appBar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.create_meal_top_app_bar_save) {
                // Parse text edits
                val title: String =
                    when {
                        binding.etTitle.text == null -> "No title"
                        binding.etTitle.text.toString().trim { it <= ' ' }
                            .isEmpty() -> "No title"
                        else -> binding.etTitle.text.toString()
                    }
                val instructions: String =
                    when {
                        binding.etInstructions.text == null -> "No instructions"
                        binding.etInstructions.text.toString().trim { it <= ' ' }
                            .isEmpty() -> "No title"
                        else -> binding.etInstructions.text.toString()
                    }

                // Add meal to database
                val meal = Meal()
                meal.title = title
                meal.instructions = instructions
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { r -> r.insertOrUpdate(meal) }
                realm.close()

                // Finish activity
                finish()
                return@OnMenuItemClickListener true
            }
            false
        })
    }
}