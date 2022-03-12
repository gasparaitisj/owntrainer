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
class DiaryEntryDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var dao: DiaryEntryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.diaryEntryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDiaryEntry() = runTest {
        val diaryEntry = DiaryEntry(
            diaryEntryId = 1,
            year = 2021,
            dayOfYear = 1,
            dayOfWeek = 1,
            monthOfYear = 1,
            dayOfMonth = 1
        )
        dao.insertDiaryEntry(diaryEntry)

        val allDiaryEntries = dao.getAllDiaryEntries().asLiveData().getOrAwaitValue()

        assertThat(allDiaryEntries).contains(diaryEntry)
    }

    @Test
    fun deleteDiaryEntry() = runTest {
        val diaryEntry = DiaryEntry(
            diaryEntryId = 1,
            year = 2021,
            dayOfYear = 1,
            dayOfWeek = 1,
            monthOfYear = 1,
            dayOfMonth = 1
        )
        dao.insertDiaryEntry(diaryEntry)
        dao.deleteDiaryEntryByYearAndDayOfYear(diaryEntry.year, diaryEntry.dayOfYear)

        val allDiaryEntries = dao.getAllDiaryEntries().asLiveData().getOrAwaitValue()

        assertThat(allDiaryEntries).doesNotContain(diaryEntry)
    }
}