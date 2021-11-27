package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.Meal
import io.realm.Realm
import timber.log.Timber

class AddMealToDiaryViewModel constructor(private val bundle: Bundle) : ViewModel() {
    private val realm: Realm = Realm.getDefaultInstance()
    private val primaryKey: String = bundle.getString("primaryKey").toString()

    val meals: List<Meal> = realm.where(Meal::class.java).findAll()

    fun addMealToDiary(meal: Meal) {
        realm.executeTransaction {
            it.where(DiaryEntry::class.java)
                .equalTo("yearAndDayOfYear", primaryKey)
                .findFirst()
                ?.meals
                ?.add(meal)
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}