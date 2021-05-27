package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.ActivityCreateMealItemBinding
import io.realm.Realm

class CreateMealItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateMealItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_meal_item)
        initUi()
    }

    private fun initUi() {
        // Back button
        binding.createMealTopAppBar.setNavigationOnClickListener { onBackPressed() }

        // Save button
        binding.createMealTopAppBar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.create_meal_top_app_bar_save) {
                // Parse text edits
                val title: String =
                    when {
                        binding.createMealTitleInputText.text == null -> "No title"
                        binding.createMealTitleInputText.text.toString().trim { it <= ' ' }
                            .isEmpty() -> "No title"
                        else -> binding.createMealTitleInputText.text.toString()
                    }
                val instructions: String =
                    when {
                        binding.createMealInstructionsText.text == null -> "No instructions"
                        binding.createMealTitleInputText.text.toString().trim { it <= ' ' }
                            .isEmpty() -> "No title"
                        else -> binding.createMealInstructionsText.text.toString()
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