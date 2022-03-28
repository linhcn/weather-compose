package com.linhcn.weather.data.local

import com.linhcn.weather.data.local.entities.Weather
import com.linhcn.weather.utils.getApplicableDate
import java.util.*
import javax.inject.Inject

interface LocalDataSource {
    suspend fun getWeathersOnDate(date: Date): List<Weather>

    suspend fun insertWeathers(weathers: List<Weather>)
}

class LocalDataSourceImpl @Inject constructor(private val weatherDb: WeatherDatabase) :
    LocalDataSource {
    override suspend fun getWeathersOnDate(date: Date): List<Weather> {
        return weatherDb.weatherDao().getWeatherOnDate(date.getApplicableDate() ?: "").toList()
    }

    override suspend fun insertWeathers(weathers: List<Weather>) {
        weatherDb.weatherDao().insertWeathers(weathers)
    }
}