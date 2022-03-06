package com.gasparaiciukas.owntrainer.repository

import com.gasparaiciukas.owntrainer.database.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val user: Flow<User>
    suspend fun updateUser(user: User)
}