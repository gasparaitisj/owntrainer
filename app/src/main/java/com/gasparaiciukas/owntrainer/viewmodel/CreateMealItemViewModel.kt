package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.Meal
import io.realm.Realm

class CreateMealItemViewModel : ViewModel() {
    fun addMealToDatabase(title: String, instructions: String) {
        // Add meal to database
        val meal = Meal()
        meal.title = parseTitle(title)
        meal.instructions = parseInstructions(instructions)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.insertOrUpdate(meal) }
        realm.close()
    }

    private fun parseTitle(title: String) : String {
        return when {
            title.trim { it <= ' ' }
                .isEmpty() -> "No title"
            else -> title
        }
    }

    private fun parseInstructions(instructions: String) : String {
        return when {
            instructions.trim { it <= ' ' }
                .isEmpty() -> "No instructions"
            else -> instructions
        }
    }
}