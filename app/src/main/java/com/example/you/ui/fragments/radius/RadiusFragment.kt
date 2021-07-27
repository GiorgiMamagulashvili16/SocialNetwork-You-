package com.example.you.ui.fragments.radius

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.posts.PostPagingAdapter
import com.example.you.databinding.RadiusFragmentBinding
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.posts.PostFragmentDirections
import com.example.you.util.Resource
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RadiusFragment : BaseFragment<RadiusFragmentBinding>(RadiusFragmentBinding::inflate) {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val postAdapter: PostPagingAdapter by lazy { PostPagingAdapter() }
    private val viewModel: RadiusViewModel by viewModels()
    private var currentPostIndex: Int? = null


    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        getLocation()
        initRecycle()
        observeAddCommentResponse()
        observePostLikes()
    }

    private fun observeLoadState() {
        lifecycleScope.launch {
            postAdapter.loadStateFlow.collect {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading)
                    showLinearLoading()
                else
                    dismissLinearLoadingDialog()
            }
        }
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
                lifecycleScope.launch {
                    viewModel.getNearbyPosts(locationResult.lastLocation).collect {
                        postAdapter.submitData(lifecycle, it)
                    }
                }
                observeLoadState()
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

    private fun observePostLikes() {
        viewModel.postLikes.observe(viewLifecycleOwner, { isLiked ->
            when (isLiked) {
                is Resource.Success -> {
                    currentPostIndex?.let { index ->
                        postAdapter.peek(index)?.apply {
                            this.isLiked = isLiked.data!!
                            this.likeLoading = false
                            if (isLiked.data) {
                                likedBy += FirebaseAuth.getInstance().uid!!
                            } else {
                                likedBy -= FirebaseAuth.getInstance().uid!!
                            }
                        }
                        postAdapter.notifyItemChanged(index)
                    }
                }
                is Resource.Error -> {
                }
                is Resource.Loading -> {
                    currentPostIndex?.let { index ->
                        postAdapter.peek(index)?.apply {
                            this.likeLoading = true
                        }
                    }
                }
            }
        })
    }

    private fun initRecycle() {
        binding.rvNearbyPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
        postAdapter.onViewCommentClick = {
            val action =
                RadiusFragmentDirections.actionRadiusFragmentToBottomSheetComments(it)
            Navigation.findNavController(requireView()).navigate(action)
        }
        postAdapter.onProfileClick = {
            findNavController().navigate(
                RadiusFragmentDirections.actionRadiusFragmentToOtherUserProfileFragment(
                    it
                )
            )
        }
        postAdapter.onCommentClick = {
            showAddCommentDialog(it)
        }
        postAdapter.onLikeClick = { post, index ->
            currentPostIndex = index
            post.isLiked = !post.isLiked
            viewModel.getPostLikes(post)
        }
        postAdapter.onLikedByClick = {
            findNavController().navigate(
                RadiusFragmentDirections.actionRadiusFragmentToBottomSheetLikes(
                    it.toTypedArray()
                )
            )
        }
    }
}