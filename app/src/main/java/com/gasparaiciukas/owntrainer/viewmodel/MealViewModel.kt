package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.Meal
import io.realm.Realm

class MealViewModel : ViewModel() {
    private val realm = Realm.getDefaultInstance()
    val meals: List<Meal> = realm.where(Meal::class.java).findAll()

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}