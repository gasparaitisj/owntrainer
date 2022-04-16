package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject internal constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    val ldResponse: LiveData<Resource<GetResponse>> get() = _ldResponse
    private val _ldResponse: MutableLiveData<Resource<GetResponse>> = MutableLiveData()

    var pageNumber = 1
    var foodsResponse: Resource<GetResponse>? = null

    fun getFoods(query: String) {
        viewModelScope.launch {
            _ldResponse.postValue(Resource.loading(null))
            val response = diaryRepository.getFoods(
                query = query,
                dataType = Constants.Api.DataType.DATATYPE_BRANDED + "," +
                        Constants.Api.DataType.DATATYPE_SR_LEGACY,
                numberOfResultsPerPage = Constants.Api.QUERY_PAGE_SIZE,
                pageSize = Constants.Api.QUERY_PAGE_SIZE,
                pageNumber = pageNumber,
                requireAllWords = true
            )
            pageNumber++
            if (foodsResponse == null) {
                foodsResponse = response
            } else {
                val oldFoods = foodsResponse?.data?.foods
                val newFoods = response.data?.foods
                if (newFoods != null) {
                    oldFoods?.addAll(newFoods)
                }
            }
            _ldResponse.postValue(foodsResponse ?: response)
        }
    }

    fun onQueryChanged() {
        pageNumber = 1
        foodsResponse = null
    }
}