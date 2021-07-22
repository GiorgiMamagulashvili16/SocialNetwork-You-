package com.example.you.ui.fragments.my_profile.edit_profile

import android.net.Uri
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.example.you.util.Resource
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
                    dismissLoadingDialog()
                    binding.apply {
                        etUserName.setText(it.data?.userName)
                        etDescription.setText(it.data?.description)
                    }
                }
                is Resource.Error -> {
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                    dismissLoadingDialog()
                }
                is Resource.Loading -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun observeUpdateUserResponse() {
        viewModel.editUserInfo.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                }
                is Resource.Error -> {
                    d("EDITTEXTRESPONSE", "${it.errorMessage}")
                    dismissLoadingDialog()
                }
                is Resource.Loading -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun setListeners() {
        binding.apply {
            ivCoverImage.setRandomCover()
            btnAddProfileImage.setOnClickListener {
                addProfileImage()
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
        if (profileImageUri == null) {
            val profileUpdate =
                ProfileUpdate(FirebaseAuth.getInstance().uid!!, username, desc, null)
            viewModel.editUserInfo(profileUpdate)
        } else {
            val profileUpdate =
                ProfileUpdate(FirebaseAuth.getInstance().uid!!, username, desc, profileImageUri)
            viewModel.editUserInfo(profileUpdate)
        }


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

}