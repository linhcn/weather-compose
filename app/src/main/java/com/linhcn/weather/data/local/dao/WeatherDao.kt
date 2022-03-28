package com.linhcn.weather.data.local.dao

import androidx.room.*
import com.linhcn.weather.data.local.entities.ConvertersTime
import com.linhcn.weather.data.local.entities.Weather
import java.util.*

@Dao
@TypeConverters(ConvertersTime::class)
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE applicable_date = :applicableDate")
    suspend fun getWeatherOnDate(applicableDate: String): Array<Weather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeathers(weathers: List<Weather>)

}