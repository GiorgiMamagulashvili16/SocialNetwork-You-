package com.example.you.ui.fragments.radius

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.PostAdapter
import com.example.you.databinding.RadiusFragmentBinding
import com.example.you.ui.base.BaseFragment
import com.example.you.util.Resource
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RadiusFragment : BaseFragment<RadiusFragmentBinding>(RadiusFragmentBinding::inflate) {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val postAdapter: PostAdapter by lazy { PostAdapter() }
    private val viewModel: RadiusViewModel by viewModels()

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        getLocation()
        observePosts()
        initRecycle()

    }

    private fun observePosts() {
        viewModel.nearbyPosts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    binding.root.isRefreshing = false
                    postAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    binding.root.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.root.isRefreshing = true
                }
            }
        })
    }

    private fun getLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(1)
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                viewModel.getNearbyPosts(locationResult.lastLocation)
                binding.root.setOnRefreshListener {
                    viewModel.getNearbyPosts(locationResult.lastLocation)
                    postAdapter.notifyDataSetChanged()
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

    private fun initRecycle() {
        binding.rvNearbyPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }
}