package com.example.chatterbox.diModule

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppContextModule {
    @Singleton
    @Provides
    fun provideAppContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }
}