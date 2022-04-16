package com.gasparaiciukas.owntrainer.di

import android.content.Context
import androidx.room.Room
import com.gasparaiciukas.owntrainer.database.AppDatabase
import com.gasparaiciukas.owntrainer.database.DiaryEntryDao
import com.gasparaiciukas.owntrainer.database.MealDao
import com.gasparaiciukas.owntrainer.database.UserDao
import com.gasparaiciukas.owntrainer.prefs.PrefsStoreImpl
import com.gasparaiciukas.owntrainer.repository.DiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Singleton
    @Provides
    fun providePrefsStore(@ApplicationContext context: Context): PrefsStoreImpl {
        return PrefsStoreImpl(context)
    }

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
    fun provideDiaryEntryDao(appDatabase: AppDatabase): DiaryEntryDao = appDatabase.diaryEntryDao()

    @Provides
    fun provideMealDao(appDatabase: AppDatabase): MealDao = appDatabase.mealDao()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Singleton
    @Provides
    fun provideDefaultDiaryRepository() = FakeDiaryRepositoryTest() as DiaryRepository

    @Singleton
    @Provides
    fun provideDefaultUserRepository() = FakeUserRepositoryTest() as UserRepository
}