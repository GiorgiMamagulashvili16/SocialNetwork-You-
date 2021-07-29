package com.example.you.ui.fragments.my_profile.edit_profile

import android.Manifest
import android.net.Uri
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.you.R
import com.example.you.databinding.EditProfileFragmentBinding
import com.example.you.extensions.setRandomCover
import com.example.you.extensions.slideUp
import com.example.you.models.user.ProfileUpdate
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<EditProfileFragmentBinding>(EditProfileFragmentBinding::inflate) {
    private val viewModel: EditProfileViewModel by viewModels()
    private var profileImageUri: Uri? = null

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        setListeners()
        observeCurrentImage()
        viewModel.getUser()
        observeUserInfo()
        observeUpdateUserResponse()
        slideUp(
            requireContext(),
            binding.etUserName,
            binding.etDescription,
            binding.ivProfileImage,
            binding.btnSave,
            binding.btnBack,
            binding.btnAddProfileImage
        )
        binding.btnBack.setOnClickListener {
            findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment())
        }
    }

    private fun observeCurrentImage() {
        viewModel.profileImage.observe(viewLifecycleOwner, {
            profileImageUri = it
        })
    }

    private fun observeUserInfo() {
        viewModel.userInfo.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    binding.apply {
                        etUserName.setText(it.data?.userName)
                        etDescription.setText(it.data?.description)
                    }
                }
                is Resource.Error -> {
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                    dismissLinearLoadingDialog()
                }
                is Resource.Loading -> {
                    showLinearLoading()
                }
            }
        })
    }

    private fun observeUpdateUserResponse() {
        viewModel.editUserInfo.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                }
                is Resource.Error -> {
                    d("EDITTEXTRESPONSE", "${it.errorMessage}")
                    dismissLinearLoadingDialog()
                }
                is Resource.Loading -> {
                    showLinearLoading()
                }
            }
        })
    }

    private fun setListeners() {
        binding.apply {
            ivCoverImage.setRandomCover()
            btnAddProfileImage.setOnClickListener {
                mediaPermissionRequest()
            }
            btnSave.setOnClickListener {
                updateProfile()
            }
        }
    }

    private fun updateProfile() {
        val username = binding.etUserName.text.toString()
        val desc = binding.etDescription.text.toString()
        d("PROFILE IMAGE IN REPO", "$profileImageUri")
        val profileUpdate =
            ProfileUpdate(FirebaseAuth.getInstance().uid!!, username, desc, profileImageUri)
        viewModel.editUserInfo(profileUpdate)
//        if (profileImageUri == null) {
//            val profileUpdate =
//                ProfileUpdate(FirebaseAuth.getInstance().uid!!, username, desc, null)
//            viewModel.editUserInfo(profileUpdate)
//        } else {
//            val profileUpdate =
//                ProfileUpdate(FirebaseAuth.getInstance().uid!!, username, desc, profileImageUri)
//            viewModel.editUserInfo(profileUpdate)
//        }


    }

    private fun addProfileImage() {
        profileImageContract.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(1, 1)
            }
        )
    }

    private val profileImageContract = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            uri?.let {
                binding.ivProfileImage.setImageURI(it)
                viewModel.getCurrentImage(it)
            }
        }
    }
    private fun mediaPermissionRequest() {
        when {
            hasCameraPermission() && hasReadExtStoragePermission() && hasWriteExtStoragePermission() -> {
                addProfileImage()
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
    private val mediaLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perm ->
            if (perm[Manifest.permission.CAMERA] == true && perm[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                perm[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
            ) {
                addProfileImage()
            }
        }

}