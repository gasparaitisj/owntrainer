package com.gasparaiciukas.owntrainer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.utils.Constants.DATABASE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities =
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
                .databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .addCallback(AppDatabaseCallback(CoroutineScope(SupervisorJob())))
                .build()
        }
    }

    // Callback for populating the database
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
            userDao.insertUser(createUser())
        }

        // Create default user
        private fun createUser(): User {
            val currentDate = LocalDate.now()
            val user = User(
                sex = "Male",
                ageInYears = 25,
                heightInCm = 180,
                weightInKg = 80.0,
                lifestyle = "Lightly active",
                currentYear = currentDate.year,
                currentMonth = currentDate.monthValue,
                currentDayOfYear = currentDate.dayOfYear,
                currentDayOfMonth = currentDate.dayOfMonth,
                currentDayOfWeek = currentDate.dayOfWeek.value
            )
            user.recalculateUserMetrics()
            return user
        }
    }
}
