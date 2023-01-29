package com.gasparaiciukas.owntrainer.utils.fragment

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@EntryPoint
@InstallIn(ActivityComponent::class)
interface MainFragmentFactoryEntryPoint {
    fun getFragmentFactory(): MainFragmentFactory
}
