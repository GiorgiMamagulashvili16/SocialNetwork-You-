package com.example.you.repositories.news

import com.example.you.models.news.CovidStatistics
import com.example.you.models.news.Weather
import com.example.you.util.Resource

interface NewsRepository {
    suspend fun getCovidInfo():Resource<CovidStatistics>
    suspend fun getWeatherInfo(lat:Double,long:Double):Resource<Weather>
}