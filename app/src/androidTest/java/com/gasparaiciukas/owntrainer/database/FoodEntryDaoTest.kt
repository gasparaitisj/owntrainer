package com.gasparaiciukas.owntrainer.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class FoodEntryDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase

    private lateinit var foodEntryDao: FoodEntryDao
    private lateinit var mealDao: MealDao

    @Before
    fun setup() {
        hiltRule.inject()
        foodEntryDao = database.foodEntryDao()
        mealDao = database.mealDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertFoodEntry() = runTest {
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        val foodEntry = FoodEntry(
            foodEntryId = 1,
            mealId = meal.mealId,
            title = "Egg",
            caloriesPer100G = 100.0,
            carbsPer100G = 100.0,
            fatPer100G = 100.0,
            proteinPer100G = 100.0,
            quantityInG = 100.0
        )

        foodEntryDao.insertFoodEntry(foodEntry)

        val allFoodEntries = foodEntryDao.getAllFoodEntries().asLiveData().getOrAwaitValue()
        assertThat(allFoodEntries).contains(foodEntry)
    }

    @Test
    fun deleteFoodEntry() = runTest {
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        val foodEntry = FoodEntry(
            foodEntryId = 1,
            mealId = meal.mealId,
            title = "Egg",
            caloriesPer100G = 100.0,
            carbsPer100G = 100.0,
            fatPer100G = 100.0,
            proteinPer100G = 100.0,
            quantityInG = 100.0
        )

        foodEntryDao.insertFoodEntry(foodEntry)
        val foodEntryId = foodEntryDao.getAllFoodEntries().asLiveData().getOrAwaitValue()[0].foodEntryId
        foodEntryDao.deleteFoodEntryById(foodEntryId)

        val allFoodEntries = foodEntryDao.getAllFoodEntries().asLiveData().getOrAwaitValue()
        assertThat(allFoodEntries).doesNotContain(foodEntry)
    }
}