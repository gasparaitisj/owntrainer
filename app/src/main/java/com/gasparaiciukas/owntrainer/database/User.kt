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
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0
    @ColumnInfo(name = "bmr")
    var bmr: Double = calculateBmr(weightInKg, heightInCm.toDouble(), ageInYears, sex)
    @ColumnInfo(name = "kcalBurnedPerStep")
    var kcalBurnedPerStep: Double = 0.0
    @ColumnInfo(name = "dailyKcalIntake")
    var dailyKcalIntake: Double = calculateDailyKcalIntake(bmr, lifestyle)
    @ColumnInfo(name = "dailyProteinIntakeInG")
    var dailyProteinIntakeInG: Double = calculateDailyProteinIntakeInG(weightInKg)
    @ColumnInfo(name = "dailyFatIntakeInG")
    var dailyFatIntakeInG: Double = calculateDailyFatIntake(dailyKcalIntake)
    @ColumnInfo(name = "dailyCarbsIntakeInG")
    var dailyCarbsIntakeInG: Double = calculateDailyCarbsIntake(dailyKcalIntake)

    // Recalculate each metric by formula
    fun recalculateUserMetrics() {
        bmr = calculateBmr(weightInKg, heightInCm.toDouble(), ageInYears, sex)
        dailyKcalIntake = calculateDailyKcalIntake(bmr, lifestyle)
        dailyProteinIntakeInG = calculateDailyProteinIntakeInG(weightInKg)
        dailyFatIntakeInG = calculateDailyFatIntake(dailyKcalIntake)
        dailyCarbsIntakeInG = calculateDailyCarbsIntake(dailyKcalIntake)
    }

    /*
    BMR - Basal Metabolic Rate
    Calculated with the Mifflin-St Jeor Equation
     */
    private fun calculateBmr(
        weightInKg: Double,
        heightInCm: Double,
        age: Int,
        sex: String
    ): Double {
        return if (sex == "Male") {
            // Men: BMR = 10W + 6.25H - 5A + 5
            10 * weightInKg + 6.25 * heightInCm - 5 * age + 5
        } else {
            // Women: BMR = 10W + 6.25H - 5A - 161
            10 * weightInKg + 6.25 * heightInCm - 5 * age - 161
        }
    }

    /*
    Daily kCal intake calculator, based on the Mifflin-St Jeor Equation
     */
    private fun calculateDailyKcalIntake(bmr: Double, lifestyle: String?): Double {
        return when (lifestyle) {
            "Sedentary" -> bmr * 1.2
            "Lightly active" -> bmr * 1.375
            "Moderately active" -> bmr * 1.55
            "Very active" -> bmr * 1.725
            "Extra active" -> bmr * 1.9
            else -> 0.0
        }
    }

    /*
    Dietary Reference Intake recommends:
    0.8 grams of protein per kg of body weight
     */
    private fun calculateDailyProteinIntakeInG(weightInKg: Double): Double {
        return weightInKg * 0.8
    }

    /*
    Dietary Reference Intake recommends:
    20% to 35% of total calories from fat
     */
    private fun calculateDailyFatIntake(dailyKcalIntake: Double): Double {
        return dailyKcalIntake * 0.35
    }

    /*
    Dietary Reference Intake recommends:
    45% to 65% of total calories from carbs
     */
    private fun calculateDailyCarbsIntake(dailyKcalIntake: Double): Double {
        return dailyKcalIntake * 0.55 / 4
    }
}