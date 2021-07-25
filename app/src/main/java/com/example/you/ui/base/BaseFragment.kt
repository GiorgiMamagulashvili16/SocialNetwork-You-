package com.example.you.ui.base

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.example.you.databinding.*
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.setDialog
import com.example.you.ui.fragments.bottom_sheets.CommentsViewModel
import com.example.you.ui.fragments.dashboard.string
import com.example.you.ui.fragments.my_profile.ProfileViewModel
import com.example.you.ui.fragments.posts.PostViewModel
import com.example.you.util.Resource

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    private var loadingDialog: Dialog? = null
    private var linearLoadingDialog: Dialog? = null
    private var deletePostDialog: Dialog? = null
    private var errorDialog: Dialog? = null
    private var addCommentDialog: Dialog? = null

    private val viewModel: ProfileViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val commentsViewModel: CommentsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(layoutInflater, container, false)
        start(layoutInflater, container)
        return binding.root
    }

    abstract fun start(inflater: LayoutInflater, viewGroup: ViewGroup?)

    protected fun requestMediaPermissions(request: ActivityResultLauncher<Array<String>>) {
        request.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    protected fun requestLocationPermissions(request: ActivityResultLauncher<Array<String>>) {
        request.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    protected fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    protected fun hasWriteExtStoragePermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    protected fun hasReadExtStoragePermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    protected fun hasFineLocationPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    protected fun hasCoarseLocationPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showLoadingDialog() {
        loadingDialog = Dialog(requireContext())
        val dialogBinding = DialogLoadingBinding.inflate(layoutInflater)
        loadingDialog!!.setDialog(dialogBinding)
        loadingDialog!!.show()
    }

    protected fun dismissLoadingDialog() {
        loadingDialog!!.hide()
    }

    protected fun showDeletePostDialog(postId: String) {
        deletePostDialog = Dialog(requireContext())
        val binding = DialogDeletePostBinding.inflate(layoutInflater)
        deletePostDialog!!.setDialog(binding)
        binding.apply {
            btnYes.setOnClickListener {
                viewModel.deletePost(postId)
            }
            btnNo.setOnClickListener {
                deletePostDialog!!.dismiss()
            }
        }
        deletePostDialog!!.show()
    }

    protected fun dismissDeletePostDialog() {
        deletePostDialog!!.dismiss()
    }

    protected fun showErrorDialog(text: String) {
        errorDialog = Dialog(requireContext())
        val binding = DialogErrorBinding.inflate(layoutInflater)
        errorDialog!!.setDialog(binding)
        binding.apply {
            tvErrorText.text = text
            btnOk.setOnClickListener {
                errorDialog!!.dismiss()
            }
        }
        errorDialog!!.show()
    }

    protected fun showLinearLoading() {
        linearLoadingDialog = Dialog(requireContext())
        val dialogBinding = DialogLinearLoadingBinding.inflate(layoutInflater)
        linearLoadingDialog!!.setDialog(dialogBinding)
        linearLoadingDialog!!.show()
    }

    protected fun dismissLinearLoadingDialog() {
        linearLoadingDialog!!.dismiss()
    }

    protected fun showAddCommentDialog(postId: String) {
        addCommentDialog = Dialog(requireContext())
        val dialogBinding = DialogAddCommentBinding.inflate(layoutInflater)

        addCommentDialog!!.setDialog(dialogBinding)
        dialogBinding.btnAddComment.setOnClickListener {
            if (dialogBinding.etComment.text.toString().isNotEmpty()) {
                postViewModel.addComment(postId, dialogBinding.etComment.text.toString())
            } else {
                createInfoSnackBar(getString(string.please_fill_all_fields), Color.RED)
            }
        }
        addCommentDialog!!.show()
    }

    private fun dismissAddCommentDialog() {
        addCommentDialog!!.dismiss()
    }

    protected fun observeAddCommentResponse() {
        postViewModel.addComment.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
                    dismissAddCommentDialog()
                }
                is Resource.Error -> {
                    dismissLoadingDialog()
                    dismissAddCommentDialog()
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                }
                is Resource.Loading -> {
                    showLoadingDialog()
                }
            }
        })
    }

}