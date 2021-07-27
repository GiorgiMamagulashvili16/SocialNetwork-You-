package com.example.you.network.news

import com.example.you.models.news.Weather
import com.example.you.util.Constants.WEATHER_API_ID
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/data/2.5/weather?")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") id: String = WEATHER_API_ID,
        @Query("units") units: String = "metric"
    ):Response<Weather>
}