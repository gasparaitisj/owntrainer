package com.gasparaiciukas.owntrainer.di

import android.content.Context
import androidx.room.Room
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class],
    replaces = [DatabaseModule::class])
object TestDatabaseModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

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

    @Singleton
    @Provides
    fun provideDefaultDiaryRepository() = FakeDiaryRepositoryTest() as DiaryRepository

    @Singleton
    @Provides
    fun provideDefaultUserRepository() = FakeUserRepositoryTest() as UserRepository
}