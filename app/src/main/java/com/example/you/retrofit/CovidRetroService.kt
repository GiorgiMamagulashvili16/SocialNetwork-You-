package com.example.you.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CovidRetroService {
    val INSTANCE: CovidRepo by lazy {
        Retrofit.Builder()
            .baseUrl("https://corona.lmao.ninja")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CovidRepo::class.java)
    }
}