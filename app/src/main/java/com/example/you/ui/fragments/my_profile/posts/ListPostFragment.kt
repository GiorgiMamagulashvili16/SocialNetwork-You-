package com.example.you.ui.fragments.my_profile.posts

import android.graphics.Color
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.DashboardGraphDirections
import com.example.you.R
import com.example.you.adapters.posts.ListPostAdapter
import com.example.you.databinding.ListPostFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.ui.fragments.my_profile.ProfileViewModel
import com.example.you.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListPostFragment : BaseFragment<ListPostFragmentBinding>(ListPostFragmentBinding::inflate) {
    private val listPostAdapter by lazy { ListPostAdapter() }
    private val viewModel: ProfileViewModel by viewModels()
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        initRec()
        viewModel.getPosts()
        observe()
        observeDeletePostResponse()
    }

    private fun observe() {
        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    listPostAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    it.errorMessage?.let { it1 -> showErrorDialog(it1) }
                }
                is Resource.Loading -> Unit
            }
        })
    }

    private fun observeDeletePostResponse() {
        viewModel.deletePostResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
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
        binding.rvListPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listPostAdapter
        }
        listPostAdapter.onDeleteClick = {
            showDeletePostDialog(it)
        }
        listPostAdapter.onViewCommentClick = {
            val action = DashboardGraphDirections.actionGlobalBottomSheetComments(it)
            Navigation.findNavController(requireView()).navigate(action)
        }
    }
}