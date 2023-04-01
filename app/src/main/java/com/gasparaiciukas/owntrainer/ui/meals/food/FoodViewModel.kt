package com.gasparaiciukas.owntrainer.ui.meals.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasparaiciukas.owntrainer.model.FoodItem
import com.gasparaiciukas.owntrainer.utils.network.GetResponse
import com.gasparaiciukas.owntrainer.utils.network.Resource
import com.gasparaiciukas.owntrainer.utils.other.Constants
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class FoodViewModel @Inject internal constructor(
    val diaryRepository: DiaryRepository,
) : ViewModel() {
    val foods: MutableStateFlow<List<FoodItem>> = MutableStateFlow(emptyList())

    private val _textSearch = MutableStateFlow("")
    private val textSearch: StateFlow<String> = _textSearch.asStateFlow()

    var pageNumber = 1
    var foodsResponse: Resource<GetResponse>? = null

    init {
        viewModelScope.launch {
            textSearch
                .debounce(DEBOUNCE_TIMEOUT_MILLIS)
                .collect { query ->
                    pageNumber = 1
                    foodsResponse = null
                    getFoods(query)
                }
        }
    }

    fun onQueryChanged(text: String) {
        _textSearch.update { text }
    }

    fun getFoods(query: String) {
        viewModelScope.launch {
            val response = diaryRepository.getFoods(
                query = query,
                dataType = Constants.Api.DataType.DATATYPE_BRANDED + "," +
                    Constants.Api.DataType.DATATYPE_SR_LEGACY,
                numberOfResultsPerPage = Constants.Api.QUERY_PAGE_SIZE,
                pageSize = Constants.Api.QUERY_PAGE_SIZE,
                pageNumber = pageNumber,
                requireAllWords = true,
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
            foods.value = foodsResponse?.data?.foods?.map { it.toFoodItem() }
                ?: response.data?.foods?.map { it.toFoodItem() }
                ?: listOf()
        }
    }

    companion object {
        const val DEBOUNCE_TIMEOUT_MILLIS = 1000L
    }
}
