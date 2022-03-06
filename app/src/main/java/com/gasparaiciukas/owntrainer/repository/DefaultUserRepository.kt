package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.database.UserDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserRepository @Inject constructor(
    private val userDao: UserDao
): UserRepository {
    override val user = userDao.getUser()
    override suspend fun updateUser(user: User) = userDao.updateUser(user)
}