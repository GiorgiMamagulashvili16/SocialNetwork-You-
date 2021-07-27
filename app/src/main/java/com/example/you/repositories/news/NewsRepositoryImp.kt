package com.example.you.repositories.news

import com.example.you.models.news.CovidStatistics
import com.example.you.models.news.Weather
import com.example.you.network.news.CovidInfoApi
import com.example.you.network.news.WeatherApi
import com.example.you.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsRepositoryImp @Inject constructor(
    private val covidInfoApi: CovidInfoApi,
    private val weatherApi: WeatherApi
) : NewsRepository {
    override suspend fun getCovidInfo(): Resource<CovidStatistics> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = covidInfoApi.getCovidInfo()
            if (result.isSuccessful) {
                val body = result.body()!!
                Resource.Success(body)
            } else {
                Resource.Error(result.errorBody().toString())
            }
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun getWeatherInfo(lat: Double, long: Double): Resource<Weather> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val result = weatherApi.getWeather(lat, long)
                if (result.isSuccessful) {
                    val body = result.body()!!
                    Resource.Success(body)
                } else {
                    Resource.Error(result.errorBody().toString())
                }
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }
}