package com.linhcn.weather.data.local.dao

import androidx.room.*
import com.linhcn.weather.data.local.entities.Converters
import com.linhcn.weather.data.local.entities.Weather
import java.util.*

@Dao
@TypeConverters(Converters::class)
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE date(applicable_date/ 1000,'unixepoch') = date(:date / 1000,'unixepoch')")
    suspend fun getWeatherOnDate(date: Date): Array<Weather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeathers(weathers: List<Weather>)

}