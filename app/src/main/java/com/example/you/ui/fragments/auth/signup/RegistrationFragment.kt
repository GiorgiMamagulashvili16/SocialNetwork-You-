package com.example.you.ui.fragments.auth.signup

import android.Manifest
import android.graphics.Color
import android.net.Uri
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
import com.example.you.extensions.slideUp
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment :
    BaseFragment<RegistrationFragmentBinding>(RegistrationFragmentBinding::inflate) {

    private val viewModel: RegistrationViewModel by viewModels()

    private var profileImageUri: Uri? = null

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        setListeners()
        observe()
    }

    private fun setListeners() {
        binding.btnAddImage.setOnClickListener {
            mediaPermissionRequest()
        }
        binding.toSignIN.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_logInFragment)
        }
        binding.btnSignUp.setOnClickListener {
            if (hasInternetConnection == true)
                signUp()
            else
                showErrorDialog(getString(string.no_internet_connection))
        }
        slideUp(
            requireContext(),
            binding.etEmail,
            binding.etPassword,
            binding.etRepeatPassword,
            binding.etUserName,
            binding.PickedPicture,
            binding.btnAddImage,
            binding.btnSignUp
        )
    }

    private fun observe() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    findNavController().navigate(R.id.action_registrationFragment_to_logInFragment)
                }
                is Resource.Error -> {
                    d("LogginError", "${it.errorMessage}")
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                    dismissLinearLoadingDialog()
                }
                is Resource.Loading -> {
                    showLinearLoading()
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
                setAspectRatio(1, 1)
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

    private fun signUp() {
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
            profileImageUri?.let {
                viewModel.signUp(
                    email,
                    password,
                    userName,
                    profileImageUri!!
                )
            } ?: createInfoSnackBar(getString(string.please_choose_image), Color.RED)
        }
    }


}