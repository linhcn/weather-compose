package com.linhcn.weather.data.remote

import com.linhcn.weather.data.local.entities.Weather
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiClient {

    @GET("location/1252431/{year}/{month}/{day}")
    suspend fun getWeathersOnDate(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int,
    ): List<Weather>
}