package com.example.you.ui.fragments.news


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.you.R
import com.example.you.databinding.NewsFragmentBinding
import com.example.you.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class NewsFragment: BaseFragment<NewsFragmentBinding>(NewsFragmentBinding::inflate) {
    private val viewModel: NewsViewModel by viewModels()
    private var isToday by Delegates.notNull<Boolean>()
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }
    private fun init(){
        viewModel.init()
        observe()
    }
    private fun setCoronaStatistics(cases: String, deaths: String, recovered: String, buttonText: String){
        binding.confirmed.text = cases
        binding.deaths.text = deaths
        binding.recovered.text = recovered
        binding.button.text = buttonText
    }
    private fun observe() {
        viewModel._jsonLiveData.observe(viewLifecycleOwner, Observer {
            isToday = false
            val totalCases = it.cases.toString()
            val totalDeaths = it.deaths.toString()
            val totalRecovered = it.recovered.toString()
            val todayCases = it.todayCases.toString()
            val todayDeaths = it.todayDeaths.toString()
            val todayRecovered = "No Info"
            setCoronaStatistics(totalCases, totalDeaths, totalRecovered, "Today")
            binding.button.setOnClickListener {
                if(isToday){
                    setCoronaStatistics(totalCases, totalDeaths, totalRecovered, "Today")
                    isToday = false
                }else{
                    setCoronaStatistics(todayCases, todayDeaths, todayRecovered, "Total")
                    isToday = true
                }
            }
        })
        viewModel._weatherLiveData.observe(viewLifecycleOwner, Observer {
            val visibility = "${it.visibility/1000}km"
            val humidity = "${it.main.humidity}%"
            val windSpeed = "${ it.wind.speed}km/h"
            binding.temperature.text = it.main.temp.toInt().toString()
            binding.visibility.text = visibility
            binding.humidity.text = humidity
            binding.wind.text = windSpeed
            binding.description.text = it.weather[0].main
            when(it.weather[0].main){
                "Thunderstorm" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_46)
                "Drizzle" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_49)
                "Rain" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_49)
                "Snow" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_47)
                "Clear" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_22)
                "Clouds" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_39)
                "Mist" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_45)
                "Smoke" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_32)
                "Haze" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_45)
                "Dust" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_32)
                "Fog" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_32)
                "Sand" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_32)
                "Ash" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_45)
                "Squall" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_45)
                "Tornado" -> binding.weatherImageView.setImageResource(R.drawable.ic_weathericon___2_46)
            }
        })
    }


}