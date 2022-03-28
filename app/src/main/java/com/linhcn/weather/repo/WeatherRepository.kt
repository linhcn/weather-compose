package com.linhcn.weather.repo

import com.linhcn.weather.data.local.LocalDataSource
import com.linhcn.weather.data.local.entities.Weather
import com.linhcn.weather.data.remote.RemoteDataSource
import com.linhcn.weather.models.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

interface WeatherRepository {
    suspend fun getWeathersOnDate(date: Date): Flow<Result<List<Weather>>>
}

class WeatherRepositoryImpl @Inject constructor(
    private val localDS: LocalDataSource,
    private val remoteDS: RemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : WeatherRepository {

    override suspend fun getWeathersOnDate(date: Date): Flow<Result<List<Weather>>> = flow {

        // handle local data
        val localWeather = localDS.getWeathersOnDate(date)
        if (localWeather.isNotEmpty()) {
            emit(Result.Success(localWeather))
        }

        // handle remote data
        val remoteWeather = remoteDS.getWeathersOnDate(date)
        if (remoteWeather is Result.Success) {
            // insert remote data to local store
            remoteWeather.data?.let {
                localDS.insertWeathers(it)
            }
        }
        if (localWeather.isEmpty()) {
            emit(remoteWeather)
        }
    }.flowOn(dispatcher)
}