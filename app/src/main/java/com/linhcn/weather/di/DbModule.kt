package com.linhcn.weather.di

import android.content.Context
import androidx.room.Room
import com.linhcn.weather.data.local.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    fun provideRoomDatabase(@ApplicationContext appContext: Context): WeatherDatabase =
        Room.databaseBuilder(
            appContext,
            WeatherDatabase::class.java,
            "local-db"
        ).build()
}