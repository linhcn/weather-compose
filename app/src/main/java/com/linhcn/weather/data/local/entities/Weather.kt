package com.linhcn.weather.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.linhcn.weather.BuildConfig
import java.util.*

@Entity
data class Weather(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "state_name") val stateName: String? = null,
    @ColumnInfo(name = "state_abbr") val stateAbbr: String? = null,
    @ColumnInfo(name = "wind_direction_compass") val windDirectionCompass: String? = null,
    @ColumnInfo(name = "created") val created: Date? = null,
    @ColumnInfo(name = "applicable_date") val applicableDate: Date? = null,
    @ColumnInfo(name = "min_temp") val minTemp: Float? = null,
    @ColumnInfo(name = "max_temp") val maxTemp: Float? = null,
    @ColumnInfo(name = "the_temp") val theTemp: Float? = null,
    @ColumnInfo(name = "wind_speed") val windSpeed: Float? = null,
    @ColumnInfo(name = "wind_direction") val windDirection: Float? = null,
    @ColumnInfo(name = "air_pressure") val airPressure: Float? = null,
    @ColumnInfo(name = "humidity") val humidity: Int? = null,
    @ColumnInfo(name = "visibility") val visibility: Float? = null,
    @ColumnInfo(name = "predictability") val predictability: Int? = null
)


fun List<Weather>.getWeatherSuitableTime(): Weather {
    val now = Date()
    var min = this[0]
    this.forEach { weather ->
        if (weather.applicableDate == null)
            return@forEach
        val compared = weather.applicableDate.time / now.time
        min.applicableDate?.let {
            if (compared < (it.time / now.time)) {
                min = weather
            }
        }

    }
    return min
}

fun Weather.getWeatherIconUrl(): String {
    return "${BuildConfig.BASE_ICON_URL}${stateAbbr}.svg"
}