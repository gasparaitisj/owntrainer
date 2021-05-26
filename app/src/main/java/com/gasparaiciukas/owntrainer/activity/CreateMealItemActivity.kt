package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm

class CreateMealItemActivity : AppCompatActivity() {
    // Views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tTitle: TextInputEditText
    private lateinit var tInstructions: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meal_item)
        initUi()
    }

    private fun initUi() {
        // Get views
        toolbar = findViewById(R.id.create_meal_top_app_bar)
        tTitle = findViewById(R.id.create_meal_title_input_text)
        tInstructions = findViewById(R.id.create_meal_instructions_text)

        // Back button
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Save button
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.create_meal_top_app_bar_save) {
                // Parse text edits
                val title: String =
                    when {
                        tTitle.text == null -> "No title"
                        tTitle.text.toString().trim { it <= ' ' }.isEmpty() -> "No title"
                        else -> tTitle.text.toString()
                    }
                val instructions: String =
                    when {
                    tInstructions.text == null -> "No instructions"
                    tTitle.text.toString().trim { it <= ' ' }.isEmpty() -> "No title"
                    else -> tInstructions.text.toString()
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