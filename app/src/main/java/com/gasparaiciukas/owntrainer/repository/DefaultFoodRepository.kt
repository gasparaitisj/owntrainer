package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.BuildConfig
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.FoodEntryDao
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.GetService
import com.gasparaiciukas.owntrainer.network.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultFoodRepository @Inject constructor(
    private val getService: GetService,
    private val foodEntryDao: FoodEntryDao,
): FoodRepository {
    override suspend fun getFoods(query: String): Resource<GetResponse> {
        return try {
            val response = getService.getFoods(BuildConfig.API_KEY, query)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("An unknown error occurred.", null)
            } else {
                Resource.error("An unknown error occurred.", null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't reach the server. Check your internet connection.", null)
        }
    }

    override suspend fun insertFood(foodEntry: FoodEntry) = foodEntryDao.insertFoodEntry(foodEntry)
    override suspend fun deleteFoodById(foodEntryId: Int) = foodEntryDao.deleteFoodEntryById(foodEntryId)
}