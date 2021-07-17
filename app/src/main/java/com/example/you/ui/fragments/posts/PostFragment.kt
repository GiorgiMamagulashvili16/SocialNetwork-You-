package com.example.you.ui.fragments.posts

import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.PostAdapter
import com.example.you.databinding.PostFragmentBinding
import com.example.you.ui.base.BaseFragment
import com.example.you.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : BaseFragment<PostFragmentBinding>(PostFragmentBinding::inflate) {
    private val viewModel: PostViewModel by viewModels()
    private val postAdapter: PostAdapter by lazy { PostAdapter() }

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        viewModel.getPosts()
        observePosts()
        initRecycleView()
        binding.root.setOnRefreshListener {
            viewModel.getPosts()
            postAdapter.notifyDataSetChanged()
        }
    }

    private fun observePosts() {
        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    binding.root.isRefreshing = false
                    d("POSTPOST","${it.data}")
                    postAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    d("POSTPOST","${it.errorMessage}")
                    binding.root.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.root.isRefreshing = true
                }
            }
        })
    }

    private fun initRecycleView() {
        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

}