package com.example.you.ui.fragments.auth.signup

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.util.Log.d
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
import com.example.you.databinding.RegistrationFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.hide
import com.example.you.extensions.show
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Resource
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegistrationFragment :
    BaseFragment<RegistrationFragmentBinding>(RegistrationFragmentBinding::inflate) {

    private val viewModel: RegistrationViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var userLocation: Location? = null

    private var profileImageUri: Uri? = null

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        setListeners()
        locationPermissionsRequest()
        observe()

    }

    private fun setListeners() {
        binding.btnAddImage.setOnClickListener {
            mediaPermissionRequest()
        }
    }

    private fun observe() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    findNavController().navigate(R.id.action_registrationFragment_to_logInFragment)
                }
                is Resource.Error -> {
                    binding.progressBar.hide()
                }
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
            }
        })
    }

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

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
                        requestMediaPermissions(permissionsLauncher)
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
                    requestMediaPermissions(permissionsLauncher)
                }
            }.show()
            else -> requestMediaPermissions(permissionsLauncher)
        }
    }

    private val cropImageContract = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            uri?.let {
                binding.PickedPicture.setImageURI(it)
                profileImageUri = it
            }
        }
    }

    private fun openMedia() {
        cropImageContract.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(4, 3)
            }
        )
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
                        requestLocationPermissions(permissionsLauncher)
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
                        requestLocationPermissions(permissionsLauncher)
                    }
                }.show()
            }
            else -> requestLocationPermissions(permissionsLauncher)
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
                binding.btnSignUp.setOnClickListener {
                    d("ProFile", "${locationResult.lastLocation}")
                    signUp(locationResult.lastLocation)
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

    private fun signUp(location: Location) {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val userName = binding.etUserName.text.toString()
        val repeatPassword = binding.etRepeatPassword.text.toString()
        d("ProFile", "$profileImageUri")
        if (email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
            createInfoSnackBar(getString(string.please_fill_all_fields), Color.RED)
        } else if (repeatPassword != password) {
            createInfoSnackBar(getString(string.password_do_not_match), Color.RED)
        } else {
            if (profileImageUri == null)
                createInfoSnackBar(getString(string.please_choose_image), Color.RED)
            else
                viewModel.signUp(
                    email,
                    password,
                    userName,
                    location.latitude,
                    location.longitude,
                    profileImageUri!!
                )

        }
    }

    override fun onPause() {
        super.onPause()
        val removeLocationUpdate =
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        removeLocationUpdate.addOnCompleteListener { task ->
            if (task.isSuccessful)
                d("RemoveLocationUpdate", "successfully removed")
            else
                d("RemoveLocationUpdate", "failure")
        }
    }

}