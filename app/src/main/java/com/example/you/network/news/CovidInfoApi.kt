package com.example.you.network.news

import com.example.you.models.news.CovidStatistics
import com.example.you.util.Resource
import retrofit2.Response
import retrofit2.http.GET

interface CovidInfoApi {
    @GET("/v2/all?yesterday")
    suspend fun getCovidInfo(): Response<CovidStatistics>
}