package com.linhcn.weather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linhcn.weather.data.local.dao.WeatherDao
import com.linhcn.weather.data.local.entities.ConvertersTime
import com.linhcn.weather.data.local.entities.Weather

@Database(entities = [Weather::class], version = 1, exportSchema = false)
@TypeConverters(ConvertersTime::class)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}