package com.example.you.ui.fragments.news

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.you.databinding.NewsFragmentBinding
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.drawable
import com.example.you.util.Resource
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

@AndroidEntryPoint
class NewsFragment : BaseFragment<NewsFragmentBinding>(NewsFragmentBinding::inflate) {
    private val viewModel: NewsViewModel by viewModels()
    private var isToday by Delegates.notNull<Boolean>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
        observeCovidInfo()
        observeWeatherInfo()
        lifecycleScope.launch {
            viewModel.getCovidInfo()
        }
    }

    private fun getLocation() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(1)
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lifecycleScope.launch {
                    viewModel.getWeather(
                        locationResult.lastLocation.latitude,
                        locationResult.lastLocation.longitude
                    )
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun observeWeatherInfo() {
        viewModel.weatherResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    val info = it.data!!
                    val visibility = "${info.visibility?.div(1000)}km"
                    val humidity = "${info.main.humidity}%"
                    val windSpeed = "${info.wind.speed}km/h"
                    binding.apply {
                        temperature.text = info.main.temp?.toInt().toString()
                        tvVisibility.text = visibility
                        tvHumidity.text = humidity
                        tvWind.text = windSpeed
                        description.text = info.weather[0].main
                        when (info.weather[0].main) {
                            "Thunderstorm" -> binding.weatherImageView.setImageResource(drawable.ic_thunder)
                            "Drizzle" -> binding.weatherImageView.setImageResource(drawable.ic_rain)
                            "Rain" -> binding.weatherImageView.setImageResource(drawable.ic_rain)
                            "Snow" -> binding.weatherImageView.setImageResource(drawable.ic_snow)
                            "Clear" -> binding.weatherImageView.setImageResource(drawable.ic_sun)
                            "Clouds" -> binding.weatherImageView.setImageResource(drawable.ic_cloud)
                            "Mist" -> binding.weatherImageView.setImageResource(drawable.ic_wind)
                            "Smoke" -> binding.weatherImageView.setImageResource(drawable.ic_cloud_sun_wind)
                            "Haze" -> binding.weatherImageView.setImageResource(drawable.ic_wind)
                            "Dust" -> binding.weatherImageView.setImageResource(drawable.ic_cloud_sun_wind)
                            "Fog" -> binding.weatherImageView.setImageResource(drawable.ic_cloud_sun_wind)
                            "Sand" -> binding.weatherImageView.setImageResource(drawable.ic_cloud_sun_wind)
                            "Ash" -> binding.weatherImageView.setImageResource(drawable.ic_wind)
                            "Squall" -> binding.weatherImageView.setImageResource(drawable.ic_wind)
                            "Tornado" -> binding.weatherImageView.setImageResource(drawable.ic_thunder)
                        }
                    }

                }
                is Resource.Error -> {
                    dismissLinearLoadingDialog()
                    it.errorMessage?.let { it1 -> showErrorDialog(it1) }
                }
                is Resource.Loading -> {
                    showLinearLoading()
                }
            }
        })
    }

    private fun observeCovidInfo() {
        viewModel.covidResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    isToday = false
                    val info = it.data!!

                    val totalCases = info.cases.toString()
                    val totalDeaths = info.deaths.toString()
                    val totalRecovered = info.recovered.toString()
                    val todayCases = info.todayCases.toString()
                    val todayDeaths = info.todayDeaths.toString()
                    val todayRecovered = "No Info"
                    setCoronaStatistics(totalCases, totalDeaths, totalRecovered, "Today")
                    binding.btnTimeIndicator.setOnClickListener {
                        isToday = if (isToday) {
                            setCoronaStatistics(totalCases, totalDeaths, totalRecovered, "Today")
                            false
                        } else {
                            setCoronaStatistics(todayCases, todayDeaths, todayRecovered, "Total")
                            true
                        }
                    }
                }
                is Resource.Error -> {
                    dismissLinearLoadingDialog()
                    it.errorMessage?.let { it1 -> showErrorDialog(it1) }
                }
                is Resource.Loading -> {
                    showLinearLoading()
                }
            }
        })
    }

    private fun setCoronaStatistics(
        cases: String,
        deaths: String,
        recovered: String,
        buttonText: String
    ) {
        binding.apply {
            confirmed.text = cases
            tvDeaths.text = deaths
            tvRecovered.text = recovered
            btnTimeIndicator.text = buttonText
        }
    }
    override fun onPause() {
        super.onPause()
        val removeLocationUpdate =
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        removeLocationUpdate.addOnCompleteListener { task ->
            if (task.isSuccessful)
                Log.d("RemoveLocationUpdate", "successfully removed")
            else
                Log.d("RemoveLocationUpdate", "failure")
        }
    }

}