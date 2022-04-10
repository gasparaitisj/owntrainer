package com.gasparaiciukas.owntrainer.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.gasparaiciukas.owntrainer.database.Lifestyle
import com.gasparaiciukas.owntrainer.database.Sex
import com.gasparaiciukas.owntrainer.database.User
import com.gasparaiciukas.owntrainer.utils.Constants
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FakeUserRepository : UserRepository {
    override var user: Flow<User> = MutableLiveData(createUser()).asFlow()

    override suspend fun updateUser(user: User) {
        this.user = MutableLiveData(user).asFlow()
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