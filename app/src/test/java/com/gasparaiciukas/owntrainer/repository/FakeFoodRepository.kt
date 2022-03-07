package com.gasparaiciukas.owntrainer.repository

import androidx.lifecycle.MutableLiveData
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource

class FakeFoodRepository : FoodRepository {

    private val foodEntries = mutableListOf<FoodEntry>()
    private val observableFoodEntries = MutableLiveData<List<FoodEntry>>()
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshLiveData() {
        observableFoodEntries.postValue(foodEntries)
    }

    override suspend fun getFoods(query: String): Resource<GetResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Error", null)
        } else {
            Resource.success(GetResponse())
        }
    }

    override suspend fun insertFood(foodEntry: FoodEntry) {
        foodEntries.add(foodEntry)
        refreshLiveData()
    }

    override suspend fun deleteFoodById(foodEntryId: Int) {
        foodEntries.removeIf { it.foodEntryId == foodEntryId }
        refreshLiveData()
    }
}