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
class ReminderDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase
    private lateinit var reminderDao: ReminderDao

    @Before
    fun setup() {
        hiltRule.inject()
        reminderDao = database.reminderDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertReminder() = runTest {
        val reminder = Reminder(
            reminderId = 1,
            title = "Breakfast",
            hour = 7,
            minute = 30
        )
        reminderDao.insertReminder(reminder)

        val reminders = reminderDao.getReminders().asLiveData().getOrAwaitValue()
        assertThat(reminders).contains(reminder)
    }

    @Test
    fun updateReminder() = runTest {
        val reminder = Reminder(
            reminderId = 1,
            title = "Breakfast",
            hour = 7,
            minute = 30
        )
        reminderDao.insertReminder(reminder)

        reminder.title = "Lunch"
        reminder.hour = 12
        reminder.minute = 0
        reminderDao.updateReminder(reminder)

        val reminders = reminderDao.getReminders().asLiveData().getOrAwaitValue()
        assertThat(reminders).contains(reminder)
    }

    @Test
    fun deleteReminder() = runTest {
        val reminder = Reminder(
            reminderId = 1,
            title = "Breakfast",
            hour = 7,
            minute = 30
        )
        reminderDao.insertReminder(reminder)
        reminderDao.deleteReminderById(reminder.reminderId)
        val reminders = reminderDao.getReminders().asLiveData().getOrAwaitValue()
        assertThat(reminders).doesNotContain(reminder)
    }
}