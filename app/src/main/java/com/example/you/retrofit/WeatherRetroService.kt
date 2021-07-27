package com.example.you.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherRetroService {
    val INSTANCE: WeatherRepo by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherRepo::class.java)
    }
}