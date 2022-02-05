package com.gasparaiciukas.owntrainer.database

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    fun getMealsWithFoodEntries() = mealDao.getMealsWithFoodEntries()
    suspend fun getMealWithFoodEntriesById(id: Int) = mealDao.getMealWithFoodEntriesById(id)
    suspend fun addMeal(meal: Meal) = mealDao.insertAll(meal)
    suspend fun deleteMealById(mealId: Int) = mealDao.deleteById(mealId)
}