package com.gasparaiciukas.owntrainer.utils.di

import android.content.Context
import com.gasparaiciukas.owntrainer.utils.database.AppDatabase
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryDao
import com.gasparaiciukas.owntrainer.utils.database.DiaryEntryWithMealsDao
import com.gasparaiciukas.owntrainer.utils.database.FoodEntryDao
import com.gasparaiciukas.owntrainer.utils.database.MealDao
import com.gasparaiciukas.owntrainer.utils.database.ReminderDao
import com.gasparaiciukas.owntrainer.utils.database.UserDao
import com.gasparaiciukas.owntrainer.utils.network.GetService
import com.gasparaiciukas.owntrainer.utils.prefs.PrefsStoreImpl
import com.gasparaiciukas.owntrainer.utils.repository.DefaultUserRepository
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.utils.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun providePrefsStore(@ApplicationContext context: Context): PrefsStoreImpl {
        return PrefsStoreImpl(context)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideDiaryEntryDao(appDatabase: AppDatabase): DiaryEntryDao {
        return appDatabase.diaryEntryDao()
    }

    @Provides
    fun provideDiaryEntryWithMealsDao(appDatabase: AppDatabase): DiaryEntryWithMealsDao {
        return appDatabase.diaryEntryWithMealsDao()
    }

    @Provides
    fun provideFoodEntryDao(appDatabase: AppDatabase): FoodEntryDao {
        return appDatabase.foodEntryDao()
    }

    @Provides
    fun provideMealDao(appDatabase: AppDatabase): MealDao {
        return appDatabase.mealDao()
    }

    @Provides
    fun provideReminderDao(appDatabase: AppDatabase): ReminderDao {
        return appDatabase.reminderDao()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Singleton
    @Provides
    fun provideDefaultDiaryRepository(
        diaryEntryDao: DiaryEntryDao,
        diaryEntryWithMealsDao: DiaryEntryWithMealsDao,
        mealDao: MealDao,
        foodEntryDao: FoodEntryDao,
        getService: GetService,
    ) = DiaryRepository(
        diaryEntryDao,
        diaryEntryWithMealsDao,
        mealDao,
        foodEntryDao,
        getService,
    )

    @Singleton
    @Provides
    fun provideDefaultUserRepository(
        userDao: UserDao,
        reminderDao: ReminderDao,
        prefsStore: PrefsStoreImpl,
    ) = DefaultUserRepository(userDao, reminderDao, prefsStore) as UserRepository
}
