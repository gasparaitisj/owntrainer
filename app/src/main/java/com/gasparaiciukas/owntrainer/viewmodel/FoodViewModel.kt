package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.repository.DefaultFoodRepository
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource
import com.gasparaiciukas.owntrainer.network.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject internal constructor(
    private val foodRepository: DefaultFoodRepository
) : ViewModel() {
    private val _ldFoods: MutableLiveData<List<Food>> = MutableLiveData(mutableListOf())
    val ldFoods: LiveData<List<Food>> get() = _ldFoods
    private val _ldResponse: MutableLiveData<Resource<GetResponse>> = MutableLiveData()
    val ldResponse: LiveData<Resource<GetResponse>> get() = _ldResponse
    var foods = listOf<Food>()

    fun getFoods(query: String) {
        viewModelScope.launch {
            val response = foodRepository.getFoods(query)
            if (response.status == Status.SUCCESS) {
                _ldFoods.value = response.data?.foods
            } else {
                _ldResponse.value = response
            }
        }
    }
}