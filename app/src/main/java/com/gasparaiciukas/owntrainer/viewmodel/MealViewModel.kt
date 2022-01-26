package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.DiaryEntryRepository
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealRepository
import com.gasparaiciukas.owntrainer.database.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject internal constructor(
    private val mealRepository: MealRepository
) : ViewModel() {
    val ldMeals = mealRepository.getMeals().asLiveData()
    lateinit var meals : List<Meal>

    fun deleteMealFromMeals(position: Int) {
//        realm.executeTransaction {
//            it.where(Meal::class.java)
//                .equalTo("title", meals[position].title)
//                .findFirst()
//                ?.deleteFromRealm()
//        }
    }
}