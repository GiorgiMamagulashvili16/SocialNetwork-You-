package com.example.you.ui.fragments.my_profile.posts

import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.you.adapters.GridPostAdapter
import com.example.you.databinding.GridPostFragmentBinding
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.my_profile.ProfileViewModel
import com.example.you.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GridPostFragment : BaseFragment<GridPostFragmentBinding>(GridPostFragmentBinding::inflate) {

    private val gridPostAdapter by lazy { GridPostAdapter() }
    private val viewModel: ProfileViewModel by viewModels()
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        viewModel.getPosts()
        observePosts()
        initRec()
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

    private fun initRec() {
        binding.rvGridPosts.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = gridPostAdapter
        }
    }

}