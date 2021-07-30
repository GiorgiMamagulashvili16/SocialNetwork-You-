package com.example.you.ui.fragments.addpost

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.you.R
import com.example.you.databinding.AddPostFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.slideUp
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Constants.POST_TYPE_FOR_ALL
import com.example.you.util.Constants.POST_TYPE_FOR_RADIUS
import com.example.you.util.Resource
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AddPostFragment : BaseFragment<AddPostFragmentBinding>(AddPostFragmentBinding::inflate) {

    private var postImageView: Uri? = null
    private val viewModel: AddPostViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationPermissionsRequest()
        setListener()
        observePostResponse()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        slideUp(
            requireContext(),
            binding.btnAddPost,
            binding.ivPostImage,
            binding.tvAddPicture,
            binding.tvPostText,
            binding.chipGroup
        )
    }

    private fun setListener() {
        binding.btnAddPost.setOnClickListener {

        }
        binding.tvAddPicture.setOnClickListener {
            mediaPermissionRequest()
        }
    }

    private fun observePostResponse() {
        viewModel.addPostResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
                    findNavController().navigate(R.id.action_addPostFragment2_to_profileFragment)
                }
                is Resource.Error -> {
                    dismissLoadingDialog()
                }
                is Resource.Loading -> {
                    it.errorMessage?.let { it1 -> showErrorDialog(it1) }
                    showLoadingDialog()
                }
            }
        })
    }

    private fun mediaPermissionRequest() {
        when {
            hasCameraPermission() && hasReadExtStoragePermission() && hasWriteExtStoragePermission() -> {
                openMedia()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                Snackbar.make(
                    binding.root,
                    getString(string.app_needs_this_permission),
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(getString(string.ok)) {
                        requestMediaPermissions(mediaLocationLauncher)
                    }
                }.show()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> Snackbar.make(
                binding.root,
                getString(string.app_needs_this_permission),
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(getString(string.ok)) {
                    requestMediaPermissions(mediaLocationLauncher)
                }
            }.show()
            else -> requestMediaPermissions(mediaLocationLauncher)
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
                    requireView(),
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
                    requireView(),
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

    private fun addPost(location: Location) {
        var postType = POST_TYPE_FOR_ALL
        val chipForRadius = binding.chipsForRadius
        val chipForAll = binding.chipForAll
        val chipGroup = binding.chipGroup

        val postText = binding.tvPostText.text.toString()
        if (postText.isBlank()) {
            createInfoSnackBar(getString(string.please_fill_all_fields), Color.RED)
        } else {
            if (chipForAll.isChecked || chipForRadius.isChecked) {
                when (chipGroup.checkedChipId) {
                    R.id.chipForAll -> postType = POST_TYPE_FOR_ALL
                    R.id.chipsForRadius -> postType = POST_TYPE_FOR_RADIUS
                }
                chipGroup.setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        R.id.chipsForRadius -> postType = POST_TYPE_FOR_RADIUS
                        R.id.chipForAll -> postType = POST_TYPE_FOR_ALL
                    }
                }
                viewModel.addPost(
                    postImageView!!,
                    postText,
                    postType,
                    location.latitude,
                    location.longitude
                )
            } else {
                createInfoSnackBar(getString(string.please_choose_post_publish_type), Color.RED)
            }
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
                binding.btnAddPost.setOnClickListener {
                    postImageView?.let {
                        addPost(locationResult.lastLocation)
                    } ?:  createInfoSnackBar(getString(string.please_choose_post_image), Color.RED)
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

    private val cropImageContract = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            binding.ivPostImage.setImageURI(uri)
            postImageView = uri
        } else {
//            val error = result.error
        }
    }

    private fun openMedia() {
        cropImageContract.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(16, 9)
            }
        )
    }

    private val mediaLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perm ->
            if (perm[Manifest.permission.CAMERA] == true && perm[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                perm[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
            ) {
                openMedia()
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