package com.linhcn.weather.di

import com.linhcn.weather.data.local.LocalDataSource
import com.linhcn.weather.data.local.LocalDataSourceImpl
import com.linhcn.weather.data.remote.RemoteDataSource
import com.linhcn.weather.data.remote.RemoteDataSourceImpl
import com.linhcn.weather.repo.WeatherRepository
import com.linhcn.weather.repo.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(remoteDataSourceImpl: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(localDataSourceImpl: LocalDataSourceImpl): LocalDataSource

    @Binds
    @Singleton
    abstract fun bingWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

}