package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.Meal
import io.realm.Realm

class AddMealToDiaryViewModel constructor(private val bundle: Bundle) : ViewModel() {
    val realm: Realm = Realm.getDefaultInstance()
    val meals: List<Meal> = realm.where(Meal::class.java).findAll()
    private val primaryKey: String = bundle.getString("primaryKey").toString()

    fun selectMeal(meal: Meal) {
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
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}