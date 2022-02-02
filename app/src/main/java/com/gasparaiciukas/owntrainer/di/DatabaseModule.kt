package com.gasparaiciukas.owntrainer.di

import android.content.Context
import com.gasparaiciukas.owntrainer.database.*
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
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
}