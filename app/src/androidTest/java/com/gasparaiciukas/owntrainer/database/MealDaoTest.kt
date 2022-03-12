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
class MealDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var mealDao: MealDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        mealDao = database.mealDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertMeal() = runTest {
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        mealDao.insertMeal(meal)

        val allMeals = mealDao.getAllMeals().asLiveData().getOrAwaitValue()
        assertThat(allMeals).contains(meal)
    }

    @Test
    fun deleteMeal() = runTest {
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        mealDao.insertMeal(meal)

        val mealId = mealDao.getAllMeals().asLiveData().getOrAwaitValue()[0].mealId
        mealDao.deleteMealById(mealId)

        val allMeals = mealDao.getAllMeals().asLiveData().getOrAwaitValue()
        assertThat(allMeals).doesNotContain(meal)
    }
}