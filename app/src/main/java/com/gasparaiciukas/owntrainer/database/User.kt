package com.gasparaiciukas.owntrainer.database

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.utils.Constants


@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) var userId: Int = 0,
    @ColumnInfo(name = "sex") var sex: Int,
    @ColumnInfo(name = "ageInYears") var ageInYears: Int,
    @ColumnInfo(name = "heightInCm") var heightInCm: Int,
    @ColumnInfo(name = "weightInKg") var weightInKg: Double,
    @ColumnInfo(name = "lifestyle") var lifestyle: Int,
    @ColumnInfo(name = "year") var year: Int,
    @ColumnInfo(name = "month") var month: Int,
    @ColumnInfo(name = "dayOfYear") var dayOfYear: Int,
    @ColumnInfo(name = "dayOfMonth") var dayOfMonth: Int,
    @ColumnInfo(name = "dayOfWeek") var dayOfWeek: Int,
) {
    @ColumnInfo(name = "kcalBurnedPerStep")
    var kcalBurnedPerStep: Double = 0.0

    private val bmr: Double
        get() = calculateBmr(
            weightInKg,
            heightInCm.toDouble(),
            ageInYears,
            Sex.values()[sex]
        )
    val dailyKcalIntake: Double get() = calculateDailyKcalIntake(bmr, Lifestyle.values()[lifestyle])
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
        sex: Sex
    ): Double {
        return if (sex == Sex.MALE) {
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
    private fun calculateDailyKcalIntake(bmr: Double, lifestyle: Lifestyle?): Double {
        return when (lifestyle) {
            Lifestyle.SEDENTARY -> bmr * 1.2
            Lifestyle.LIGHTLY_ACTIVE -> bmr * 1.375
            Lifestyle.MODERATELY_ACTIVE -> bmr * 1.55
            Lifestyle.VERY_ACTIVE -> bmr * 1.725
            Lifestyle.EXTRA_ACTIVE -> bmr * 1.9
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

enum class Sex(val value: String) {
    MALE(Constants.EN.male) {
        override fun selectionDescription(context: Context): String {
            return context.getString(R.string.male)
        }
    },
    FEMALE(Constants.EN.female) {
        override fun selectionDescription(context: Context): String {
            return context.getString(R.string.female)
        }
    };

    abstract fun selectionDescription(context: Context): String
}

enum class Lifestyle(val value: String) {
    SEDENTARY(Constants.EN.sedentary) {
        override fun selectionDescription(context: Context): String {
            return context.getString(R.string.sedentary)
        }
    },
    LIGHTLY_ACTIVE(Constants.EN.lightly_active) {
        override fun selectionDescription(context: Context): String {
            return context.getString(R.string.lightly_active)
        }
    },
    MODERATELY_ACTIVE(Constants.EN.moderately_active) {
        override fun selectionDescription(context: Context): String {
            return context.getString(R.string.moderately_active)
        }
    },
    VERY_ACTIVE(Constants.EN.very_active) {
        override fun selectionDescription(context: Context): String {
            return context.getString(R.string.very_active)
        }
    },
    EXTRA_ACTIVE(Constants.EN.extra_active) {
        override fun selectionDescription(context: Context): String {
            return context.getString(R.string.extra_active)
        }
    };

    abstract fun selectionDescription(context: Context): String
}