package com.gasparaiciukas.owntrainer.utils.repository

import com.gasparaiciukas.owntrainer.settings.AppearanceMode
import com.gasparaiciukas.owntrainer.utils.database.Reminder
import com.gasparaiciukas.owntrainer.utils.database.ReminderDao
import com.gasparaiciukas.owntrainer.utils.database.User
import com.gasparaiciukas.owntrainer.utils.database.UserDao
import com.gasparaiciukas.owntrainer.utils.prefs.PrefsStoreImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val reminderDao: ReminderDao,
    private val prefsStore: PrefsStoreImpl
) : UserRepository {
    override val user = userDao.getUser()
    override suspend fun updateUser(user: User) = userDao.updateUser(user)

    override fun getReminders() = reminderDao.getReminders()
    override suspend fun insertReminder(reminder: Reminder) = reminderDao.insertReminder(reminder)
    override suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)
    override suspend fun deleteReminderById(id: Int) = reminderDao.deleteReminderById(id)

    override fun getAppearanceMode(): Flow<Int> = prefsStore.getAppearanceMode()
    override suspend fun setAppearanceMode(appearanceMode: AppearanceMode) =
        prefsStore.setAppearanceMode(appearanceMode)
}
