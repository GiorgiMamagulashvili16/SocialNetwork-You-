package com.example.you.ui.fragments.addpost

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import com.example.you.util.Resource
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
        slideUp(requireContext(),binding.btnAddPost,binding.ivPostImage,binding.tvAddPicture,binding.tvPostText)
    }

    private fun setListener() {
        binding.btnAddPost.setOnClickListener {
            if (postImageView != null) {
                addPost()
            }
        }
        binding.tvAddPicture.setOnClickListener {
            openMedia()
        }
    }

    private fun observePostResponse() {
        viewModel.addPostResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()

                    findNavController().navigate(R.id.action_global_dashboardFragment)
                }
                is Resource.Error -> {
                    dismissLoadingDialog()
                }
                is Resource.Loading -> {
                    createLoadingDialog()
                }
            }
        })
    }

    private fun addPost() {
        val postText = binding.tvPostText.text.toString()
        if (postText.isBlank()) {
            createInfoSnackBar(getString(string.please_fill_all_fields), Color.RED)
        } else {
            viewModel.addPost(postImageView!!, postText)
        }
    }

    private val cropImageContract = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            binding.ivPostImage.setImageURI(uri)
            postImageView = uri
        } else {
            val error = result.error
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