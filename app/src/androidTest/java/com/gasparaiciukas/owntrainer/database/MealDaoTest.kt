package com.gasparaiciukas.owntrainer.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
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
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class MealDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase

    private lateinit var mealDao: MealDao

    @Before
    fun setup() {
        hiltRule.inject()
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