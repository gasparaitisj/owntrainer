package com.gasparaiciukas.owntrainer.database

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    val user = userDao.getUser()
    suspend fun updateUser(user: User) = userDao.updateUser(user)
}