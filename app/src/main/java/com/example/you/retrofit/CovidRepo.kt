package com.example.you.retrofit

import com.example.you.models.covid.CovidStatistics
import retrofit2.Response
import retrofit2.http.GET

interface CovidRepo {
    @GET("/v2/all?yesterday")
    suspend fun getResponse(): Response<CovidStatistics>
}