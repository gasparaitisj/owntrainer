package com.gasparaiciukas.owntrainer.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FoodEntryDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var foodEntryDao: FoodEntryDao
    private lateinit var mealDao: MealDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        foodEntryDao = database.foodEntryDao()
        mealDao = database.mealDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertFoodEntry() = runTest {
        val meal = Meal("Omelette", "Put egg in pan")
        val foodEntry = FoodEntry(meal.mealId, "Egg", 100.0, 100.0, 100.0, 100.0, 100.0)

        foodEntryDao.insertFoodEntry(foodEntry)

        val allFoodEntries = foodEntryDao.getAllFoodEntries().asLiveData().getOrAwaitValue()
        assertThat(allFoodEntries).contains(foodEntry)
    }

    @Test
    fun deleteFoodEntry() = runTest {
        val meal = Meal("Omelette", "Put egg in pan")
        val foodEntry = FoodEntry(meal.mealId, "Egg", 100.0, 100.0, 100.0, 100.0, 100.0)

        foodEntryDao.insertFoodEntry(foodEntry)
        val foodEntryId = foodEntryDao.getAllFoodEntries().asLiveData().getOrAwaitValue()[0].foodEntryId
        foodEntryDao.deleteFoodEntryById(foodEntryId)

        val allFoodEntries = foodEntryDao.getAllFoodEntries().asLiveData().getOrAwaitValue()
        assertThat(allFoodEntries).doesNotContain(foodEntry)
    }
}