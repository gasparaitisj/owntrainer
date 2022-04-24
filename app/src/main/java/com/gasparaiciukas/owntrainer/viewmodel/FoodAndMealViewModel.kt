package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.fragment.FoodAndMealFragmentArgs
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodAndMealViewModel @Inject internal constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var index = FoodAndMealFragmentArgs.fromSavedStateHandle(savedStateHandle).index
    var isFirstTime = true
}