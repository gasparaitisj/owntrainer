package com.gasparaiciukas.owntrainer.meals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodAndMealViewModel @Inject internal constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var index = FoodAndMealFragmentArgs.fromSavedStateHandle(savedStateHandle).index
    var isFirstTime = true
}
