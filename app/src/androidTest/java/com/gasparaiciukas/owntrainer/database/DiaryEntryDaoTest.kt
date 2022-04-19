package com.gasparaiciukas.owntrainer.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class DiaryEntryDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: AppDatabase

    private lateinit var dao: DiaryEntryDao

    @Before
    fun setup() {
        hiltRule.inject()
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

        val allDiaryEntries = dao.getAllDiaryEntries().first()

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

        val allDiaryEntries = dao.getAllDiaryEntries().first()

        assertThat(allDiaryEntries).doesNotContain(diaryEntry)
    }
}