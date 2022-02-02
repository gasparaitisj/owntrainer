package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject internal constructor(
    private val mealRepository: MealRepository
) : ViewModel() {
    val ldMeals = mealRepository.getMealsWithFoodEntries().asLiveData()
    lateinit var meals : List<MealWithFoodEntries>

    fun deleteMealFromMeals(position: Int) {
//        realm.executeTransaction {
//            it.where(Meal::class.java)
//                .equalTo("title", meals[position].title)
//                .findFirst()
//                ?.deleteFromRealm()
//        }
    }
}