package com.example.you.ui.fragments.my_profile.posts

import android.app.Dialog
import android.graphics.Color
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.you.DashboardGraphDirections
import com.example.you.R
import com.example.you.adapters.posts.GridPostAdapter
import com.example.you.databinding.DialogGridPostBinding
import com.example.you.databinding.GridPostFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.getShapeableImage
import com.example.you.extensions.setDialog
import com.example.you.models.post.Post
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.ui.fragments.my_profile.ProfileFragmentDirections
import com.example.you.ui.fragments.my_profile.ProfileViewModel
import com.example.you.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GridPostFragment : BaseFragment<GridPostFragmentBinding>(GridPostFragmentBinding::inflate) {

    private val gridPostAdapter by lazy { GridPostAdapter() }
    private val viewModel: ProfileViewModel by viewModels()

    private var postDialog: Dialog? = null

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        viewModel.getPosts()
        observeDeletePostResponse()
        observePosts()
        initRec()
        observePosts()
    }

    private fun observePosts() {
        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    gridPostAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    d("GRIDPOSTRESPONSE", "${it.errorMessage}")
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun observeDeletePostResponse() {
        viewModel.deletePostResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
                    postDialog?.dismiss()
                    dismissDeletePostDialog()
                    findNavController().navigate(R.id.action_global_dashboardFragment)
                    createInfoSnackBar(getString(string.successfully_deleted), Color.GREEN)
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

    private fun initRec() {
        binding.rvGridPosts.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = gridPostAdapter
        }
        gridPostAdapter.onImageClick = {
            showPostDialog(it)
        }
    }

    private fun showPostDialog(post: Post) {
        postDialog = Dialog(requireContext())
        val binding = DialogGridPostBinding.inflate(layoutInflater)
        postDialog!!.setDialog(binding)
        binding.apply {
            tvPostText.text = post.text
            ivPostImage.getShapeableImage(post.postImageUrl)
        }
        binding.btnDeletePost.setOnClickListener {
            showDeletePostDialog(post.postId)
        }
        binding.btnViewComments.setOnClickListener {
            val action = DashboardGraphDirections.actionGlobalBottomSheetComments(post.postId)
            Navigation.findNavController(requireView()).navigate(action)
        }
        postDialog!!.show()
    }

}