package com.example.you.ui.fragments.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.posts.PostPagingAdapter
import com.example.you.databinding.PostFragmentBinding
import com.example.you.ui.base.BaseFragment
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : BaseFragment<PostFragmentBinding>(PostFragmentBinding::inflate) {
    private val viewModel: PostViewModel by viewModels()
    private val postAdapter: PostPagingAdapter by lazy { PostPagingAdapter() }
    private var currentPostIndex: Int? = null

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        initRecycleView()
        observeAddCommentResponse()
        observePostLikes()
        getPosts()
        setAdapterListeners()
    }

    private fun getPosts() {
        lifecycleScope.launch {
            viewModel.getAllPost().collect {
                postAdapter.submitData(lifecycle, it)
            }
        }
        lifecycleScope.launch {
            postAdapter.loadStateFlow.collect {
                if ( it.refresh is LoadState.Loading || it.append is LoadState.Loading)
                    showLinearLoading()
                else
                   dismissLinearLoadingDialog()

            }
        }
    }

    private fun setAdapterListeners() {
        postAdapter.onProfileClick = {
            findNavController().navigate(
                PostFragmentDirections.actionPostFragmentToOtherUserProfileFragment(
                    it
                )
            )
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
        postAdapter.onLikedByClick = {
            findNavController().navigate(
                PostFragmentDirections.actionPostFragmentToBottomSheetLikes(
                    it.toTypedArray()
                )
            )
        }
    }


    private fun observePostLikes() {
        viewModel.postLikes.observe(viewLifecycleOwner, { isLiked ->
            when (isLiked) {
                is Resource.Success -> {
                    currentPostIndex?.let { index ->
                        postAdapter.peek(index)?.apply {
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
                        postAdapter.peek(index)?.apply {
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
    }
}