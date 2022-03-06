package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource

interface FoodRepository {
    suspend fun getFoods(query: String): Resource<GetResponse>
    suspend fun insertFood(foodEntry: FoodEntry)
    suspend fun deleteFoodById(foodEntryId: Int)
}