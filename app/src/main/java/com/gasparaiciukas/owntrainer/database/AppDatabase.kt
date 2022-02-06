package com.gasparaiciukas.owntrainer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(entities =
        [
            DiaryEntry::class,
            DiaryEntryMealCrossRef::class,
            FoodEntry::class,
            Meal::class,
            User::class
        ],
        version = 1,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun diaryEntryWithMealsDao(): DiaryEntryWithMealsDao
    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun mealDao(): MealDao
    abstract fun userDao(): UserDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, "owntrainer")
                .addCallback(AppDatabaseCallback(CoroutineScope(SupervisorJob())))
                .build()
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            instance?.let { database ->
                scope.launch {
                    populateDatabase(database.userDao())
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao) {
            // Add first user
            userDao.insertAll(createUser())
        }

        private fun createUser(): User {
            val sex = "Male"
            val age = 25
            val height = 180
            val weight = 80.0
            val lifestyle = "Lightly active"
            val currentYear = LocalDate.now().year
            val currentMonth = LocalDate.now().monthValue
            val currentDayOfYear = LocalDate.now().dayOfYear
            val currentDayOfMonth = LocalDate.now().dayOfMonth
            val currentDayOfWeek = LocalDate.now().dayOfWeek.value

            val user = User(sex, age, height, weight, lifestyle, currentYear, currentMonth, currentDayOfYear, currentDayOfMonth, currentDayOfWeek)
            user.stepLengthInCm = user.calculateStepLengthInCm(height.toDouble(), sex)
            user.bmr = user.calculateBmr(weight, height.toDouble(), age, sex)
            user.kcalBurnedPerStep =
                user.calculateKcalBurnedPerStep(weight, height.toDouble(), user.avgWalkingSpeedInKmH)
            user.dailyKcalIntake = user.calculateDailyKcalIntake(user.bmr, lifestyle)
            user.dailyCarbsIntakeInG = user.calculateDailyCarbsIntake(user.dailyKcalIntake)
            user.dailyFatIntakeInG = user.calculateDailyFatIntake(user.dailyKcalIntake)
            user.dailyProteinIntakeInG = user.calculateDailyProteinIntakeInG(weight)
            return user
        }
    }
}
