package com.gasparaiciukas.owntrainer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class User(
    @ColumnInfo(name = "sex") var sex: String,
    @ColumnInfo(name = "ageInYears") var ageInYears: Int,
    @ColumnInfo(name = "heightInCm") var heightInCm: Int,
    @ColumnInfo(name = "weightInKg") var weightInKg: Double,
    @ColumnInfo(name = "lifestyle") var lifestyle: String,
    @ColumnInfo(name = "currentYear") var currentYear: Int,
    @ColumnInfo(name = "currentMonth") var currentMonth: Int,
    @ColumnInfo(name = "currentDayOfYear") var currentDayOfYear: Int,
    @ColumnInfo(name = "currentDayOfMonth") var currentDayOfMonth: Int,
    @ColumnInfo(name = "currentDayOfWeek") var currentDayOfWeek: Int,
) {
    @PrimaryKey(autoGenerate = true) var userId: Int = 0
    @ColumnInfo(name = "avgWalkingSpeedInKmH") var avgWalkingSpeedInKmH: Double = 5.0
    @ColumnInfo(name = "dailyStepGoal") var dailyStepGoal: Int = 10000
    @ColumnInfo(name = "bmr") var bmr: Double = calculateBmr(weightInKg, heightInCm.toDouble(), ageInYears, sex)
    @ColumnInfo(name = "kcalBurnedPerStep") var kcalBurnedPerStep: Double = 0.0
    @ColumnInfo(name = "dailyKcalIntake") var dailyKcalIntake: Double = calculateDailyKcalIntake(bmr, lifestyle)
    @ColumnInfo(name = "dailyProteinIntakeInG") var dailyProteinIntakeInG: Double = calculateDailyProteinIntakeInG(weightInKg)
    @ColumnInfo(name = "dailyFatIntakeInG") var dailyFatIntakeInG: Double = calculateDailyFatIntake(dailyKcalIntake)
    @ColumnInfo(name = "dailyCarbsIntakeInG") var dailyCarbsIntakeInG: Double = calculateDailyCarbsIntake(dailyKcalIntake)
    @ColumnInfo(name = "stepLengthInCm") var stepLengthInCm: Double = calculateStepLengthInCm(heightInCm.toDouble(), sex)


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

    fun recalculateUserMetrics() {
        bmr = calculateBmr(weightInKg, heightInCm.toDouble(), ageInYears, sex)
        dailyKcalIntake = calculateDailyKcalIntake(bmr, lifestyle)
        dailyProteinIntakeInG = calculateDailyProteinIntakeInG(weightInKg)
        dailyFatIntakeInG = calculateDailyFatIntake(dailyKcalIntake)
        dailyCarbsIntakeInG = calculateDailyCarbsIntake(dailyKcalIntake)
        stepLengthInCm = calculateStepLengthInCm(heightInCm.toDouble(), sex)
    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        var result = sex.hashCode()
        result = 31 * result + ageInYears
        result = 31 * result + heightInCm
        result = 31 * result + weightInKg.hashCode()
        result = 31 * result + lifestyle.hashCode()
        result = 31 * result + currentYear
        result = 31 * result + currentMonth
        result = 31 * result + currentDayOfYear
        result = 31 * result + currentDayOfMonth
        result = 31 * result + currentDayOfWeek
        result = 31 * result + userId
        result = 31 * result + avgWalkingSpeedInKmH.hashCode()
        result = 31 * result + dailyStepGoal
        result = 31 * result + bmr.hashCode()
        result = 31 * result + kcalBurnedPerStep.hashCode()
        result = 31 * result + dailyKcalIntake.hashCode()
        result = 31 * result + dailyProteinIntakeInG.hashCode()
        result = 31 * result + dailyFatIntakeInG.hashCode()
        result = 31 * result + dailyCarbsIntakeInG.hashCode()
        result = 31 * result + stepLengthInCm.hashCode()
        return result
    }
}