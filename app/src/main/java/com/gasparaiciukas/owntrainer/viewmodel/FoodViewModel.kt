package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource
import com.gasparaiciukas.owntrainer.network.Status
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject internal constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    val ldFoods: LiveData<List<Food>?> get() = _ldFoods
    val ldResponse: LiveData<Resource<GetResponse>> get() = _ldResponse

    private val _ldFoods: MutableLiveData<List<Food>?> = MutableLiveData(mutableListOf())
    private val _ldResponse: MutableLiveData<Resource<GetResponse>> = MutableLiveData()

    fun getFoods(query: String) {
        viewModelScope.launch {
            val response = diaryRepository.getFoods(query)
            _ldResponse.value = response
            if (response.status == Status.SUCCESS) {
                _ldFoods.value = response.data?.foods
            }
        }
    }
}