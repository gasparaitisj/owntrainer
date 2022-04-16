package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.Reminder
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.viewmodel.AppearanceMode
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val user: Flow<User>
    suspend fun updateUser(user: User)
    fun getReminders(): Flow<List<Reminder>>
    suspend fun insertReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminderById(id: Int)
    fun getAppearanceMode(): Flow<Int>
    suspend fun setAppearanceMode(appearanceMode: AppearanceMode)
}