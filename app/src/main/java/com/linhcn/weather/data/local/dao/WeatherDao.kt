package com.linhcn.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linhcn.weather.data.local.entities.Weather
import java.util.*

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE applicable_date = :date")
    suspend fun getWeatherOnDate(date: Date): List<Weather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)

    @Insert
    suspend fun insertWeathers(weathers: List<Weather>)

}