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
class DiaryEntryWithMealsDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var diaryEntryWithMealsDao: DiaryEntryWithMealsDao
    private lateinit var diaryEntryDao: DiaryEntryDao
    private lateinit var mealDao: MealDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        diaryEntryWithMealsDao = database.diaryEntryWithMealsDao()
        diaryEntryDao = database.diaryEntryDao()
        mealDao = database.mealDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDiaryEntryMealCrossRef() = runTest {
        // Insert test data
        val diaryEntry = DiaryEntry(
            diaryEntryId = 1,
            year = 2021,
            dayOfYear = 1,
            dayOfWeek = 1,
            monthOfYear = 1,
            dayOfMonth = 1
        )
        diaryEntryDao.insertDiaryEntry(diaryEntry)
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        mealDao.insertMeal(meal)

        // Get auto-generated ids
        val diaryEntryId = diaryEntryDao.getAllDiaryEntries().asLiveData().getOrAwaitValue()[0].diaryEntryId
        val mealId = mealDao.getAllMeals().asLiveData().getOrAwaitValue()[0].mealId

        // Check test data
        val crossRef = DiaryEntryMealCrossRef(diaryEntryId, mealId)
        diaryEntryWithMealsDao.insertDiaryEntryMealCrossRef(crossRef)

        val diaryEntryWithMeals =
            diaryEntryWithMealsDao.getDiaryEntryWithMeals(diaryEntry.year, diaryEntry.dayOfYear)
                .asLiveData().getOrAwaitValue()

        assertThat(diaryEntryWithMeals.meals).contains(meal)
    }

    @Test
    fun deleteDiaryEntryMealCrossRef() = runTest {
        // Insert test data
        val diaryEntry = DiaryEntry(
            diaryEntryId = 1,
            year = 2021,
            dayOfYear = 1,
            dayOfWeek = 1,
            monthOfYear = 1,
            dayOfMonth = 1
        )
        diaryEntryDao.insertDiaryEntry(diaryEntry)
        val meal = Meal(
            mealId = 1,
            title = "Omelette",
            instructions = "Put egg in pan"
        )
        mealDao.insertMeal(meal)

        // Get auto-generated ids
        val diaryEntryId = diaryEntryDao.getAllDiaryEntries().asLiveData().getOrAwaitValue()[0].diaryEntryId
        val mealId = mealDao.getAllMeals().asLiveData().getOrAwaitValue()[0].mealId

        // Check test data
        val crossRef = DiaryEntryMealCrossRef(diaryEntryId, mealId)
        diaryEntryWithMealsDao.insertDiaryEntryMealCrossRef(crossRef)
        diaryEntryWithMealsDao.deleteDiaryEntryMealCrossRef(diaryEntryId, mealId)

        val diaryEntryWithMeals =
            diaryEntryWithMealsDao.getDiaryEntryWithMeals(diaryEntry.year, diaryEntry.dayOfYear)
                .asLiveData().getOrAwaitValue()

        assertThat(diaryEntryWithMeals.meals).doesNotContain(meal)
    }
}