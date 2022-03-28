package com.gasparaiciukas.owntrainer.fragment

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface MainFragmentFactoryEntryPoint {
    fun getFragmentFactory(): MainFragmentFactory
}