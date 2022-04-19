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
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class UserDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        hiltRule.inject()
        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUser() = runTest {
        val currentDate = LocalDate.now()
        val user = User(
            userId = 1,
            sex = Sex.MALE.ordinal,
            ageInYears = 25,
            heightInCm = 180,
            weightInKg = 80.0,
            lifestyle = Lifestyle.LIGHTLY_ACTIVE.ordinal,
            year = currentDate.year,
            month = currentDate.monthValue,
            dayOfYear = currentDate.dayOfYear,
            dayOfMonth = currentDate.dayOfMonth,
            dayOfWeek = currentDate.dayOfWeek.value
        )
        userDao.insertUser(user)

        val userRoom = userDao.getUser().first()
        assertThat(userRoom).isEqualTo(user)
    }

    @Test
    fun updateUser() = runTest {
        val currentDate = LocalDate.now()
        var user = User(
            userId = 1,
            sex = Sex.MALE.ordinal,
            ageInYears = 25,
            heightInCm = 180,
            weightInKg = 80.0,
            lifestyle = Lifestyle.LIGHTLY_ACTIVE.ordinal,
            year = currentDate.year,
            month = currentDate.monthValue,
            dayOfYear = currentDate.dayOfYear,
            dayOfMonth = currentDate.dayOfMonth,
            dayOfWeek = currentDate.dayOfWeek.value
        )
        userDao.insertUser(user)
        user = userDao.getUser().first()
        user.heightInCm = 155
        userDao.updateUser(user)
        val userRoom = userDao.getUser().first()
        assertThat(userRoom).isEqualTo(user)
    }

    @Test
    fun deleteUser() = runTest {
        val currentDate = LocalDate.now()
        val user = User(
            userId = 1,
            sex = Sex.MALE.ordinal,
            ageInYears = 25,
            heightInCm = 180,
            weightInKg = 80.0,
            lifestyle = Lifestyle.LIGHTLY_ACTIVE.ordinal,
            year = currentDate.year,
            month = currentDate.monthValue,
            dayOfYear = currentDate.dayOfYear,
            dayOfMonth = currentDate.dayOfMonth,
            dayOfWeek = currentDate.dayOfWeek.value
        )
        userDao.insertUser(user)
        val userId = userDao.getUser().first().userId
        userDao.deleteUserById(userId)
        val userRoom = userDao.getUser().first()
        assertThat(userRoom).isEqualTo(null)
    }
}