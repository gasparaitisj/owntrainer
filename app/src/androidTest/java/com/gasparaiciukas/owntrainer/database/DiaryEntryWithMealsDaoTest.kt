package com.gasparaiciukas.owntrainer.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class DiaryEntryWithMealsDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase
    private lateinit var diaryEntryWithMealsDao: DiaryEntryWithMealsDao
    private lateinit var diaryEntryDao: DiaryEntryDao
    private lateinit var mealDao: MealDao

    @Before
    fun setup() {
        hiltRule.inject()
        diaryEntryWithMealsDao = database.diaryEntryWithMealsDao()
        diaryEntryDao = database.diaryEntryDao()
        mealDao = database.mealDao()
    }

    @After
    fun teardown() {
        database.close()
    }


}