package com.example.you.ui.fragments.posts

import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.R
import com.example.you.adapters.posts.PostAdapter
import com.example.you.databinding.PostFragmentBinding
import com.example.you.models.post.Post
import com.example.you.ui.base.BaseFragment
import com.example.you.util.Constants.BUNDLE_KEY_USER_ID
import com.example.you.util.Constants.REQUEST_KEY_USER_ID
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : BaseFragment<PostFragmentBinding>(PostFragmentBinding::inflate) {
    private val viewModel: PostViewModel by viewModels()
    private val postAdapter: PostAdapter by lazy { PostAdapter() }
    private var currentPostIndex: Int? = null

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
        observeAddCommentResponse()
        observePostLikes()
    }

    private fun observePosts() {
        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    binding.root.isRefreshing = false
                    d("POSTPOST", "${it.data}")
                    postAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    d("POSTPOST", "${it.errorMessage}")
                    binding.root.isRefreshing = false
                }
                is Resource.Loading -> {
                    it.errorMessage?.let { mes -> showErrorDialog(mes) }
                    binding.root.isRefreshing = true
                }
            }
        })
    }

    private fun observePostLikes() {
        viewModel.postLikes.observe(viewLifecycleOwner, { isLiked ->
            when (isLiked) {
                is Resource.Success -> {
                    currentPostIndex?.let { index ->
                        postAdapter.differ.currentList[index].apply {
                            this.isLiked = isLiked.data!!
                            this.likeLoading = false
                            if (isLiked.data) {
                                likedBy += FirebaseAuth.getInstance().uid!!
                            } else {
                                likedBy -= FirebaseAuth.getInstance().uid!!
                            }
                        }
                        postAdapter.notifyItemChanged(index)
                    }

                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {
                    currentPostIndex?.let { index ->
                        postAdapter.differ.currentList[index].apply {
                            this.likeLoading = true
                        }
                    }
                }
            }
        })
    }

    private fun initRecycleView() {
        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
        postAdapter.onProfileClick = {
            setFragmentResult(REQUEST_KEY_USER_ID, bundleOf(BUNDLE_KEY_USER_ID to it))
            findNavController().navigate(R.id.action_postFragment_to_otherUserProfileFragment)
        }
        postAdapter.onCommentClick = {
            showAddCommentDialog(it)
        }
        postAdapter.onViewCommentClick = {
            val action = PostFragmentDirections.actionPostFragmentToBottomSheetComments(it)
            Navigation.findNavController(requireView()).navigate(action)
        }
        postAdapter.onLikeClick = { post, index ->
            currentPostIndex = index
            post.isLiked = !post.isLiked
            viewModel.getPostLikes(post)
        }
    }
}