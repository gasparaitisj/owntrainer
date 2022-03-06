package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDiaryRepository @Inject constructor(
    private val diaryEntryDao: DiaryEntryDao,
    private val diaryEntryWithMealsDao: DiaryEntryWithMealsDao,
    private val mealDao: MealDao
): DiaryRepository {
    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntry) =
        diaryEntryDao.insertDiaryEntry(diaryEntry)

    override suspend fun removeDiaryEntry(year: Int, dayOfYear: Int) =
        diaryEntryDao.deleteDiaryEntryByYearAndDayOfYear(year, dayOfYear)

    override fun getDiaryEntry(year: Int, dayOfYear: Int) =
        diaryEntryDao.getDiaryEntryByYearAndDayOfYear(year, dayOfYear)

    override fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int) =
        diaryEntryWithMealsDao.getDiaryEntryWithMeals(year, dayOfYear)

    override suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef) =
        diaryEntryWithMealsDao.insertDiaryEntryMealCrossRef(crossRef)

    override suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int) =
        diaryEntryWithMealsDao.deleteDiaryEntryMealCrossRef(diaryEntryId, mealId)

    override fun getMealsWithFoodEntries() =
        mealDao.getMealsWithFoodEntries()

    override suspend fun getMealWithFoodEntriesById(id: Int) =
        mealDao.getMealWithFoodEntriesById(id)

    override suspend fun addMeal(meal: Meal) =
        mealDao.insertMeal(meal)

    override suspend fun deleteMealById(mealId: Int) =
        mealDao.deleteMealById(mealId)
}