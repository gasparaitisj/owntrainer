package com.gasparaiciukas.owntrainer.viewmodel

import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.database.User
import io.realm.Realm
import timber.log.Timber

class ProfileViewModel : ViewModel() {
    var sex: String = "Male"
    var age = 0
    var height = 0
    var weight = 0.0
    var lifestyle: String = "Lightly active"
    private val realm: Realm = Realm.getDefaultInstance()

    init {
        loadData()
    }

    private fun loadData() {
        val user = realm.where(User::class.java).findFirst()
        if (user != null) {
            sex = user.sex
            age = user.ageInYears
            height = user.heightInCm
            weight = user.weightInKg
            lifestyle = user.lifestyle
        }
    }

    fun writeUserToDatabase() {
        val user = User()
        user.userId = "user"
        user.ageInYears = age
        user.sex = sex
        user.heightInCm = height
        user.weightInKg = weight
        user.lifestyle = lifestyle
        user.stepLengthInCm = user.calculateStepLengthInCm(height.toDouble(), sex)
        user.bmr = user.calculateBmr(weight, height.toDouble(), age, sex)
        user.kcalBurnedPerStep =
            user.calculateKcalBurnedPerStep(weight, height.toDouble(), user.avgWalkingSpeedInKmH)
        user.dailyKcalIntake = user.calculateDailyKcalIntake(user.bmr, lifestyle)
        user.dailyCarbsIntakeInG = user.calculateDailyCarbsIntake(user.dailyKcalIntake)
        user.dailyFatIntakeInG = user.calculateDailyFatIntake(user.dailyKcalIntake)
        user.dailyProteinIntakeInG = user.calculateDailyProteinIntakeInG(weight)
        realm.executeTransaction {
            it.insertOrUpdate(user)
            Timber.d("Transaction ended!")
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}