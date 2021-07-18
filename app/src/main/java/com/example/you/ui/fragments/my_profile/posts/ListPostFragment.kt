package com.example.you.ui.fragments.my_profile.posts

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.ListPostAdapter
import com.example.you.databinding.ListPostFragmentBinding
import com.example.you.ui.base.BaseFragment
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
    }

    private fun observe() {
        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    listPostAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    Log.d("GRIDPOSTRESPONSE", "${it.errorMessage}")
                }
                is Resource.Loading -> {

                }
            }
        })
    }

    private fun initRec() {
        binding.rvListPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listPostAdapter
        }
    }
}