package com.example.you.ui.fragments.addpost

import android.Manifest
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPostFragment : BaseFragment<AddPostFragmentBinding>(AddPostFragmentBinding::inflate) {

    private var postImageView: Uri? = null
    private val viewModel: AddPostViewModel by viewModels()
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
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
            if (postImageView != null) {
                addPost()
            } else {
                createInfoSnackBar(getString(string.please_choose_post_image), Color.RED)
            }
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

    private fun addPost() {
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
                viewModel.addPost(postImageView!!, postText, postType)
            } else {
                createInfoSnackBar(getString(string.please_choose_post_publish_type), Color.RED)
            }
        }
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
}