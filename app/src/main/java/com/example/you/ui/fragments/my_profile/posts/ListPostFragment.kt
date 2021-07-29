package com.example.you.ui.fragments.my_profile.posts

import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.DashboardGraphDirections
import com.example.you.adapters.posts.PostAdapter
import com.example.you.databinding.DialogDeletePostBinding
import com.example.you.databinding.ListPostFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.setDialog
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.ui.fragments.my_profile.ProfileFragmentDirections
import com.example.you.ui.fragments.my_profile.ProfileViewModel
import com.example.you.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListPostFragment : BaseFragment<ListPostFragmentBinding>(ListPostFragmentBinding::inflate) {
    private val postAdapter by lazy { PostAdapter() }
    private val viewModel: ProfileViewModel by viewModels()
    private var deletePostDialog: Dialog? = null

    private var currentPostId = ""
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        initRec()
        viewModel.getPosts()
        observeDeletePostResponse()
        observePostResponse()
        observeDeleteCommentByPostResponse()
    }

    private fun observePostResponse() {
        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    postAdapter.differ.submitList(it.data)
                    dismissLinearLoadingDialog()
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


    private fun observeDeletePostResponse() {
        viewModel.deletePostResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
                    deletePostDialog?.dismiss()
                    viewModel.deleteCommentsByPost(currentPostId)
                }
                is Resource.Error -> {
                    dismissLoadingDialog()
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                }
                is Resource.Loading -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun observeDeleteCommentByPostResponse() {
        viewModel.deleteCommentsByPost.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
                    findNavController().navigate(DashboardGraphDirections.actionGlobalProfileFragment())
                    createInfoSnackBar(getString(string.successfully_deleted), Color.GREEN)
                }
                is Resource.Error -> {
                    dismissLoadingDialog()
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                }
                is Resource.Loading -> {
                    showLinearLoading()
                }
            }
        })
    }

    private fun showDeletePostDialog(postId: String) {
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

    private fun initRec() {
        binding.rvListPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
        postAdapter.onDeleteClick = {
            showDeletePostDialog(it)
            currentPostId = it
        }
        postAdapter.onViewCommentClick = {
            val action = DashboardGraphDirections.actionGlobalBottomSheetComments(it)
            Navigation.findNavController(requireView()).navigate(action)
        }
        postAdapter.onCommentClick = {
            showAddCommentDialog(it)
        }
    }
}