package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.database.FoodRepository
import com.gasparaiciukas.owntrainer.network.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject internal constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
    val ldFoods: MutableLiveData<List<Food>> = MutableLiveData(mutableListOf())
    var foods = mutableListOf<Food>()

    fun getFoods(query: String) {
        viewModelScope.launch {
            ldFoods.value = foodRepository.getFoods(query)
        }
    }
}