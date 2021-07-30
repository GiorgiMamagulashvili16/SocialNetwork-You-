package com.example.you.ui.fragments.radius

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.posts.PostPagingAdapter
import com.example.you.databinding.DialogErrorBinding
import com.example.you.databinding.RadiusFragmentBinding
import com.example.you.extensions.setDialog
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Resource
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RadiusFragment : BaseFragment<RadiusFragmentBinding>(RadiusFragmentBinding::inflate) {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

    private val postAdapter: PostPagingAdapter by lazy { PostPagingAdapter() }
    private val viewModel: RadiusViewModel by viewModels()
    private var currentPostIndex: Int? = null
    private var permDialog: Dialog? = null


    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        lifecycleScope.launch {
            delay(1000)
            locationPermissionsRequest()
        }

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
    private fun locationPermissionsRequest() {
        when {
            hasFineLocationPermission() && hasCoarseLocationPermission() -> {
                getLocation()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Snackbar.make(
                    binding.root,
                    getString(string.app_needs_this_permission),
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(getString(string.ok)) {
                        requestLocationPermissions(locationPermissionsLauncher)
                    }
                }.show()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                Snackbar.make(
                    binding.root,
                    getString(string.app_needs_this_permission),
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(getString(string.ok)) {
                        requestLocationPermissions(locationPermissionsLauncher)
                    }
                }.show()
            }
            else -> requestLocationPermissions(locationPermissionsLauncher)
        }
    }
    private val locationPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perm ->
            if (perm[Manifest.permission.ACCESS_FINE_LOCATION] == true && perm[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                getLocation()
            }
        }
    private fun showPermDialog(message: String) {
        permDialog = Dialog(requireContext())
        val dialogBinding = DialogErrorBinding.inflate(layoutInflater)
        permDialog?.setDialog(dialogBinding)
        dialogBinding.btnOk.setOnClickListener {
            requestLocationPermissions(permissionsLauncher)
        }
        dialogBinding.tvErrorText.text = message
        permDialog?.show()
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
        locationCallback?.let {
            val removeLocationUpdate =
                fusedLocationProviderClient.removeLocationUpdates(it)
            removeLocationUpdate.addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Log.d("RemoveLocationUpdate", "successfully removed")
                else
                    Log.d("RemoveLocationUpdate", "failure")
            }
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