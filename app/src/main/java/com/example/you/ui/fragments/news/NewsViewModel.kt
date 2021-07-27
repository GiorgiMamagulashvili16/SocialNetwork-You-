package com.example.you.ui.fragments.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.covid.CovidStatistics
import com.example.you.models.weather.Weather
import com.example.you.retrofit.CovidRetroService
import com.example.you.retrofit.WeatherRetroService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewsViewModel  @Inject constructor(): ViewModel() {
    private val jsonLiveData = MutableLiveData<CovidStatistics>().apply {
        mutableListOf<CovidStatistics>()
    }
    val _jsonLiveData = jsonLiveData

    private val weatherLiveData = MutableLiveData<Weather>().apply {
        mutableListOf<Weather>()
    }
    val _weatherLiveData = weatherLiveData

    fun init() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getJson()
            }
        }
    }
    private suspend fun getJson() {
        val result = CovidRetroService.INSTANCE.getResponse()
        if (result.isSuccessful) {
            val items = result.body()
            jsonLiveData.postValue(items)
        }
        val weatherResult = WeatherRetroService.INSTANCE.getResponse()
        if(weatherResult.isSuccessful){
            val weatherItems = weatherResult.body()
            weatherLiveData.postValue(weatherItems)
        }
    }
}