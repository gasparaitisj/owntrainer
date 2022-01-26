package com.gasparaiciukas.owntrainer.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class BundleViewModelFactory(private val bundle: Bundle) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(AddMealToDiaryViewModel::class.java)) {
//            return AddMealToDiaryViewModel(bundle) as T
//        }
        if (modelClass.isAssignableFrom(MealItemViewModel::class.java)) {
            return MealItemViewModel(bundle) as T
        }
        if (modelClass.isAssignableFrom(NetworkFoodItemViewModel::class.java)) {
            return NetworkFoodItemViewModel(bundle) as T
        }
        if (modelClass.isAssignableFrom(SelectMealItemViewModel::class.java)) {
            return SelectMealItemViewModel(bundle) as T
        }
        if (modelClass.isAssignableFrom(DatabaseFoodItemViewModel::class.java)) {
            return DatabaseFoodItemViewModel(bundle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}