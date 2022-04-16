package com.gasparaiciukas.owntrainer.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.gasparaiciukas.owntrainer.database.Lifestyle
import com.gasparaiciukas.owntrainer.database.Reminder
import com.gasparaiciukas.owntrainer.database.Sex
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.viewmodel.AppearanceMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class FakeUserRepositoryTest : UserRepository {
    override var user: Flow<User> = MutableLiveData(createUser()).asFlow()
    private val reminders: MutableStateFlow<List<Reminder>> = MutableStateFlow(listOf())
    private val appearanceMode = MutableStateFlow(AppearanceMode.SYSTEM_DEFAULT.ordinal)

    override suspend fun updateUser(user: User) {
        this.user = MutableLiveData(user).asFlow()
    }

    override fun getReminders(): Flow<List<Reminder>> = reminders

    override suspend fun insertReminder(reminder: Reminder) {
        reminders.value = (reminders.value + reminder).toMutableList()
    }

    override suspend fun updateReminder(reminder: Reminder) {
        val list = mutableListOf<Reminder>()
        for (r in reminders.value) {
            if (r.reminderId == reminder.reminderId) {
                list.add(reminder)
            } else {
                list.add(r)
            }
        }
        reminders.value = list
    }

    override suspend fun deleteReminderById(id: Int) {
        val list = reminders.value.toMutableList()
        list.removeIf { it.reminderId == id }
        reminders.value = list
    }

    override fun getAppearanceMode(): Flow<Int> = appearanceMode

    override suspend fun setAppearanceMode(appearanceMode: AppearanceMode) {
        this.appearanceMode.value = appearanceMode.ordinal
    }

    // Create default user
    private fun createUser(): User {
        val currentDate = LocalDate.now()
        return User(
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
    }
}