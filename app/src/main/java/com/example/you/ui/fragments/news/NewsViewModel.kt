package com.example.you.ui.fragments.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.news.CovidStatistics
import com.example.you.models.news.Weather
import com.example.you.repositories.news.NewsRepositoryImp
import com.example.you.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepositoryImp
) : ViewModel() {
    private val _covidResponse by lazy {
        MutableLiveData<Resource<CovidStatistics>>()
    }
    val covidResponse: LiveData<Resource<CovidStatistics>> = _covidResponse

    private val _weatherResponse by lazy {
        MutableLiveData<Resource<Weather>>()
    }
    val weatherResponse: LiveData<Resource<Weather>> = _weatherResponse

    fun getWeather(lat: Double, long: Double) = viewModelScope.launch {
        _weatherResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _weatherResponse.postValue(newsRepository.getWeatherInfo(lat, long))
        }
    }

    fun getCovidInfo() = viewModelScope.launch {
        _covidResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _covidResponse.postValue(newsRepository.getCovidInfo())
        }
    }


}