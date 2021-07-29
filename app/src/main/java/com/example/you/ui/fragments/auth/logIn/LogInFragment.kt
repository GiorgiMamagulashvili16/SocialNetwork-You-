package com.example.you.ui.fragments.auth.logIn

import android.Manifest
import android.graphics.Color
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.you.MainGraphDirections
import com.example.you.R
import com.example.you.databinding.LogInFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.slideUp
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : BaseFragment<LogInFragmentBinding>(LogInFragmentBinding::inflate) {
    private val viewModel: LogInViewModel by viewModels()

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        setListeners()
        observe()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        slideUp(
            requireContext(),
            binding.imageView,
            binding.etEmail,
            binding.textView1,
            binding.textView2,
            binding.etPassword,
            binding.btnSignIn
        )
    }

    private fun setListeners() {
        binding.btnSignIn.setOnClickListener {
            logIn()
        }
        binding.tvRegister.setOnClickListener {
            locationPermissionsRequest()

        }
    }

    private fun locationPermissionsRequest() {
        when {
            hasFineLocationPermission() && hasCoarseLocationPermission() -> {
                findNavController().navigate(MainGraphDirections.actionGlobalRegistrationFragment())
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
                findNavController().navigate(MainGraphDirections.actionGlobalRegistrationFragment())
            }
        }


    private fun logIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            createInfoSnackBar(getString(string.please_fill_all_fields), Color.RED)
        } else {
            viewModel.logIn(email, password)
        }
    }

    private fun observe() {
        viewModel.logInResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    findNavController().navigate(R.id.action_logInFragment_to_dashboardFragment)
                }
                is Resource.Error -> {
                    d("LogginError","${it.errorMessage}")
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                    dismissLinearLoadingDialog()
                }
                is Resource.Loading -> {
                    showLinearLoading()

                }
            }
        })
    }
}