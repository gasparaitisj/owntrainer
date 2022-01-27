package com.gasparaiciukas.owntrainer.di

import com.gasparaiciukas.owntrainer.network.GetService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideGetService(): GetService {
        return GetService.create()
    }

}