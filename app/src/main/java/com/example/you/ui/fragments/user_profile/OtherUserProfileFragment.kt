package com.example.you.ui.fragments.user_profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.R
import com.example.you.adapters.posts.OtherUserPostAdapter
import com.example.you.databinding.OtherUserProfileFragmentBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.extensions.setRandomCover
import com.example.you.extensions.slideUp
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtherUserProfileFragment :
    BaseFragment<OtherUserProfileFragmentBinding>(OtherUserProfileFragmentBinding::inflate) {

    private val viewModel: OtherUserProfileViewModel by viewModels()
    private val postAdapter: OtherUserPostAdapter by lazy { OtherUserPostAdapter() }
    private var currentPostIndex: Int? = null
    val args: OtherUserProfileFragmentArgs by navArgs()

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        lifecycleScope.launch {
            viewModel.getUserPosts(args.uid)
            viewModel.getUser(args.uid)
        }
        initRec()
        observeUser()
        observeUserPosts()
        setListeners()
        observePostLikes()
    }

    private fun setListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_otherUserProfileFragment_to_postFragment)
        }
        binding.btnSendMessage.setOnClickListener {
            findNavController().navigate(
                OtherUserProfileFragmentDirections.actionOtherUserProfileFragmentToChatFragment(args.uid)
            )
        }
        slideUp(
            requireContext(),
            binding.btnBack,
            binding.ivProfileImage,
            binding.tvDescription,
            binding.tvUserName,
            binding.rvPosts,
            binding.tvPostQuantity
        )
        binding.ivCoverImage.setRandomCover()
    }

    private fun observeUserPosts() {
        viewModel.posts.observe(viewLifecycleOwner, { posts ->
            when (posts) {
                is Resource.Success -> {
                    postAdapter.differ.submitList(posts.data)
                    setUserPostListSize(posts.data!!.size)
                }
                is Resource.Error -> {
                    posts.errorMessage?.let { message -> showErrorDialog(message) }
                }
                is Resource.Loading -> Unit
            }
        })
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner, { user ->
            when (user) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    setUserData(
                        user.data?.profileImageUrl!!,
                        user.data.userName,
                        user.data.description
                    )
                }
                is Resource.Error -> {
                    dismissLinearLoadingDialog()
                    user.errorMessage?.let { message -> showErrorDialog(message) }
                }
                is Resource.Loading -> {
                    showLinearLoading()
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
                    isLiked.errorMessage?.let { showErrorDialog(it) }
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

    private fun setUserData(image: String, username: String, desc: String) {
        binding.apply {
            tvDescription.text = desc
            tvUserName.text = username
            ivProfileImage.getShapeableImage(image)
        }
    }

    private fun setUserPostListSize(size: Int) {
        binding.tvPostQuantity.text = when {
            size <= 0 -> {
                getString(string.no_post_yet)
            }
            size == 1 -> {
                getString(string.post_list_size, 1, "post")
            }
            else -> {
                getString(string.post_list_size, size, "posts")
            }
        }
    }

    private fun initRec() {
        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
        adapterListeners()
    }

    private fun adapterListeners() {
        postAdapter.onViewCommentClick = {
            val action =
                OtherUserProfileFragmentDirections.actionOtherUserProfileFragmentToBottomSheetComments(
                    it
                )
            Navigation.findNavController(requireView()).navigate(action)
        }
        postAdapter.onLikeClick = { post, index ->
            currentPostIndex = index
            post.isLiked = !post.isLiked
            viewModel.getPostLikes(post)
        }
        postAdapter.onCommentClick = {
            showAddCommentDialog(it)
        }
        postAdapter.onLikedByClick = { likes ->
            val action =
                OtherUserProfileFragmentDirections.actionOtherUserProfileFragmentToBottomSheetLikes(
                    likes.toTypedArray()
                )
            Navigation.findNavController(requireView()).navigate(action)
        }
    }
}