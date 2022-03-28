package com.gasparaiciukas.owntrainer.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.Resource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FakeDiaryRepositoryTest : DiaryRepository {

    val diaryEntries = mutableListOf<DiaryEntry>()
    private val diaryEntryMealCrossRefs = mutableListOf<DiaryEntryMealCrossRef>()
    private val meals = mutableListOf<Meal>()
    var mealsWithFoodEntries = mutableListOf<MealWithFoodEntries>()
    private var foodEntries = mutableListOf<FoodEntry>()
    private var shouldReturnNetworkError = false

    fun setFoodEntries(foodEntries: MutableList<FoodEntry>) {
        this.foodEntries = foodEntries
    }

    override suspend fun insertDiaryEntry(diaryEntry: DiaryEntry) {
        diaryEntries.add(diaryEntry)
    }

    override suspend fun deleteDiaryEntry(year: Int, dayOfYear: Int) {
        diaryEntries.removeIf { it.year == year && it.dayOfYear == dayOfYear }
    }

    override fun getDiaryEntry(year: Int, dayOfYear: Int): Flow<DiaryEntry> {
        val date = LocalDate.ofYearDay(year, dayOfYear)
        val diaryEntry = DiaryEntry(
            year = date.year,
            dayOfYear = date.dayOfYear,
            dayOfWeek = date.dayOfWeek.value,
            monthOfYear = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )
        return MutableLiveData(diaryEntry).asFlow()
    }

    override fun getDiaryEntryWithMeals(year: Int, dayOfYear: Int): Flow<DiaryEntryWithMeals> {
        val date = LocalDate.ofYearDay(year, dayOfYear)
        val diaryEntry = DiaryEntry(
            year = date.year,
            dayOfYear = date.dayOfYear,
            dayOfWeek = date.dayOfWeek.value,
            monthOfYear = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )
        val diaryEntryMeals = mutableListOf<Meal>()
        for (meal in meals) {
            val crossRef = DiaryEntryMealCrossRef(
                diaryEntries.first { it.year == year && it.dayOfYear == dayOfYear }.diaryEntryId,
                meal.mealId
            )
            if (diaryEntryMealCrossRefs.contains(crossRef)) {
                diaryEntryMeals.add(meal)
            }
        }
        return MutableLiveData(
            DiaryEntryWithMeals(
                diaryEntry,
                diaryEntryMeals
            )
        ).asFlow()
    }

    override suspend fun insertDiaryEntryMealCrossRef(crossRef: DiaryEntryMealCrossRef) {
        diaryEntryMealCrossRefs.add(crossRef)
    }

    override suspend fun deleteDiaryEntryMealCrossRef(diaryEntryId: Int, mealId: Int) {
        diaryEntryMealCrossRefs.removeIf { it.diaryEntryId == diaryEntryId && it.mealId == mealId }
    }

    override fun getAllMealsWithFoodEntries(): Flow<List<MealWithFoodEntries>> {
        return MutableLiveData(mealsWithFoodEntries).asFlow()
    }

    override suspend fun getMealWithFoodEntriesById(id: Int): MealWithFoodEntries? {
        return try {
            mealsWithFoodEntries.first { it.meal.mealId == id }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    override suspend fun insertMeal(meal: Meal) {
        meals.add(meal)
        mealsWithFoodEntries.add(MealWithFoodEntries(meal, foodEntries))
    }

    override suspend fun deleteMealById(mealId: Int) {
        meals.removeIf { it.mealId == mealId }
        mealsWithFoodEntries.removeIf { it.meal.mealId == mealId }
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getFoods(query: String): Resource<GetResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Error", null)
        } else {
            Resource.success(
                GetResponse(
                    foods = listOf(
                        Food(
                            fdcId = 454004,
                            description = "APPLE",
                            lowercaseDescription = "apple",
                            dataType = "Branded",
                            gtinUpc = "867824000001",
                            publishedDate = "2019-04-01",
                            brandOwner = "TREECRISP 2 GO",
                            ingredients = "CRISP APPLE.",
                            marketCountry = "United States",
                            foodCategory = "Pre-Packaged Fruit & Vegetables",
                            allHighlightFields = "<b>Ingredients</b>: CRISP <em>APPLE</em>.",
                            score = 932.4247,
                            foodNutrients = listOf(
                                FoodNutrient(
                                    nutrientId = 1003,
                                    nutrientName = "Protein",
                                    nutrientNumber = "203",
                                    unitName = "G",
                                    derivationCode = "LCCS",
                                    derivationDescription = "Calculated from value per serving size measure",
                                    value = 0.0
                                ),
                                FoodNutrient(
                                    nutrientId = 1005,
                                    nutrientName = "Carbohydrate, by difference",
                                    nutrientNumber = "205",
                                    unitName = "G",
                                    derivationCode = "LCCS",
                                    derivationDescription = "Calculated from value per serving size measure",
                                    value = 14.3
                                ),
                                FoodNutrient(
                                    nutrientId = 1008,
                                    nutrientName = "Energy",
                                    nutrientNumber = "208",
                                    unitName = "G",
                                    derivationCode = "LCCS",
                                    derivationDescription = "Calculated from value per serving size measure",
                                    value = 52.0
                                ),
                                FoodNutrient(
                                    nutrientId = 1004,
                                    nutrientName = "Total lipid (fat)",
                                    nutrientNumber = "204",
                                    unitName = "G",
                                    derivationCode = "LCSL",
                                    derivationDescription = "Calculated from a less than value per serving size measure",
                                    value = 0.65
                                ),
                            )
                        )
                    )
                )
            )
        }
    }

    override fun getAllFoodEntries(): Flow<List<FoodEntry>> {
        return MutableLiveData(foodEntries).asFlow()
    }

    override suspend fun insertFood(foodEntry: FoodEntry) {
        foodEntries.add(foodEntry)
    }

    override suspend fun deleteFoodById(foodEntryId: Int) {
        foodEntries.removeIf { it.foodEntryId == foodEntryId }
    }
}