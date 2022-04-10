package com.gasparaiciukas.owntrainer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gasparaiciukas.owntrainer.utils.Constants


@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) var userId: Int = 0,
    @ColumnInfo(name = "sex") var sex: String,
    @ColumnInfo(name = "ageInYears") var ageInYears: Int,
    @ColumnInfo(name = "heightInCm") var heightInCm: Int,
    @ColumnInfo(name = "weightInKg") var weightInKg: Double,
    @ColumnInfo(name = "lifestyle") var lifestyle: String,
    @ColumnInfo(name = "year") var year: Int,
    @ColumnInfo(name = "month") var month: Int,
    @ColumnInfo(name = "dayOfYear") var dayOfYear: Int,
    @ColumnInfo(name = "dayOfMonth") var dayOfMonth: Int,
    @ColumnInfo(name = "dayOfWeek") var dayOfWeek: Int,
) {
    @ColumnInfo(name = "kcalBurnedPerStep")
    var kcalBurnedPerStep: Double = 0.0

    private val bmr: Double get() = calculateBmr(weightInKg, heightInCm.toDouble(), ageInYears, sex)
    val dailyKcalIntake: Double get() = calculateDailyKcalIntake(bmr, lifestyle)
    val dailyProteinIntakeInG: Double get() = calculateDailyProteinIntakeInG(weightInKg)
    val dailyFatIntakeInG: Double get() = calculateDailyFatIntake(dailyKcalIntake)
    val dailyCarbsIntakeInG: Double get() = calculateDailyCarbsIntake(dailyKcalIntake)

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
        return if (sex == Constants.Data.SEX_MALE) {
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
            Constants.Data.LIFESTYLE_SEDENTARY -> bmr * 1.2
            Constants.Data.LIFESTYLE_LIGHTLY_ACTIVE -> bmr * 1.375
            Constants.Data.LIFESTYLE_MODERATELY_ACTIVE -> bmr * 1.55
            Constants.Data.LIFESTYLE_VERY_ACTIVE -> bmr * 1.725
            Constants.Data.LIFESTYLE_EXTRA_ACTIVE -> bmr * 1.9
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
    1g fat = 9 kcal
     */
    private fun calculateDailyFatIntake(dailyKcalIntake: Double): Double {
        return dailyKcalIntake * 0.35 / 9
    }

    /*
    Dietary Reference Intake recommends:
    45% to 65% of total calories from carbs
    1g carbs = 4 kcal
     */
    private fun calculateDailyCarbsIntake(dailyKcalIntake: Double): Double {
        return dailyKcalIntake * 0.55 / 4
    }
}