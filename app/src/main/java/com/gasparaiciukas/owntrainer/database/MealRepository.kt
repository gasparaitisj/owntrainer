package com.gasparaiciukas.owntrainer.database

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    fun getMeals() = mealDao.getAll()
    suspend fun addMeal(meal: Meal) = mealDao.insertAll(meal)
}