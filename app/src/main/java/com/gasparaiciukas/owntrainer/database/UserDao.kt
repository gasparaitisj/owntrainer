package com.gasparaiciukas.owntrainer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User>

    @Insert
    suspend fun insertAll(vararg user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    fun delete(user: User)
}