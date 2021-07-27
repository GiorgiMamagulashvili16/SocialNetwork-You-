package com.example.you.retrofit


import com.example.you.models.weather.Weather
import retrofit2.Response
import retrofit2.http.GET


interface WeatherRepo {
    @GET("/data/2.5/weather?&lat=41.6&lon=44.8&appid=d700f91c7d83f5bfe2a776a28ba28f02&units=metric")
    suspend fun getResponse(): Response<Weather>
}