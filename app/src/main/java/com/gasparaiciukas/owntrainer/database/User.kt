package com.gasparaiciukas.owntrainer.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class User : RealmObject() {
    @PrimaryKey
    @Required
    lateinit var userId: String

    // Basic information
    var sex: String = ""
    var ageInYears = 0
    var heightInCm = 0
    var weightInKg = 0.0
    var lifestyle: String = ""
    var avgWalkingSpeedInKmH: Double = 5.0
    var dailyStepGoal: Int = 10000

    // Calculated information using formulas
    var bmr: Double = 0.0
    var kcalBurnedPerStep: Double = 0.0
    var dailyKcalIntake: Double = 0.0
    var dailyProteinIntakeInG: Double = 0.0
    var dailyFatIntakeInG: Double = 0.0
    var dailyCarbsIntakeInG: Double = 0.0
    var stepLengthInCm: Double = 0.0

    fun calculateBmr(weightInKg: Double, heightInCm: Double, age: Int, sex: String): Double {
        return if (sex == "Male") 10 * weightInKg + 6.25 * heightInCm - 5 * age + 5 else 10 * weightInKg + 6.25 * heightInCm - 5 * age - 161
    }

    fun calculateDailyKcalIntake(bmr: Double, lifestyle: String?): Double {
        return when (lifestyle) {
            "Sedentary" -> bmr * 1.2
            "Lightly active" -> bmr * 1.375
            "Moderately active" -> bmr * 1.55
            "Very active" -> bmr * 1.725
            "Extra active" -> bmr * 1.9
            else -> 0.0
        }
    }

    fun calculateKcalBurnedPerStep(weightInKg: Double, heightInCm: Double, avgWalkingSpeedInKmH: Double): Double {
        return (0.035 * weightInKg + avgWalkingSpeedInKmH / heightInCm * 0.029 * weightInKg) / 100
    }

    fun calculateDailyProteinIntakeInG(weightInKg: Double): Double {
        return weightInKg
    }

    fun calculateDailyFatIntake(dailyKcalIntake: Double): Double {
        return dailyKcalIntake * 0.3 / 9
    }

    fun calculateDailyCarbsIntake(dailyKcalIntake: Double): Double {
        return dailyKcalIntake * 0.55 / 4
    }

    fun calculateStepLengthInCm(heightInCm: Double, sex: String): Double {
        return if (sex == "Male") 0.415 * heightInCm else 0.413 * heightInCm
    }
}