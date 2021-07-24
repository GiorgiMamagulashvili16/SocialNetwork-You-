package com.example.you.ui.fragments.my_profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.you.R
import com.example.you.adapters.posts.PostPagerAdapter
import com.example.you.databinding.ProfileFragmentBinding
import com.example.you.extensions.getImageFromUrl
import com.example.you.extensions.setRandomCover
import com.example.you.extensions.slideUp
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.drawable
import com.example.you.ui.fragments.dashboard.string
import com.example.you.ui.fragments.my_profile.posts.GridPostFragment
import com.example.you.ui.fragments.my_profile.posts.ListPostFragment
import com.example.you.util.Resource
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileFragmentBinding>(ProfileFragmentBinding::inflate) {

    private lateinit var postPagerAdapter: PostPagerAdapter
    private val viewModel: ProfileViewModel by viewModels()
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        viewModel.apply {
            getCurrentUser()
            getPosts()
        }
        setListeners()
        initPagerAndTab()
        observeCurrentUser()
        observePostListSize()
        slideUp(requireContext(), binding.ivProfileImage, binding.tvDescription, binding.tvUserName)
    }

    private fun setListeners() {
        binding.ivCoverImage.setRandomCover()
        binding.btnBack.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionGlobalPostFragment())
        }
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }


    private fun observePostListSize() {
        viewModel.postListSize.observe(viewLifecycleOwner, {
            binding.apply {
                when (it) {
                    0 -> tvPostQuantity.text = getString(string.no_post_yet)
                    1 -> tvPostQuantity.text = getString(string.post_list_size, it, "Post")
                    else -> tvPostQuantity.text = getString(string.post_list_size, it, "Posts")
                }
            }
        })
    }

    private fun observeCurrentUser() {
        viewModel.user.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLinearLoadingDialog()
                    setUserData(it.data!!.description, it.data.userName, it.data.profileImageUrl)
                }
                is Resource.Error -> {
                    dismissLinearLoadingDialog()
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                }
                is Resource.Loading -> {
                    showLinearLoading()
                }
            }
        })
    }

    private fun setUserData(desc: String, userName: String, image: String) {
        binding.apply {
            tvDescription.text = desc
            tvUserName.text = userName
            ivProfileImage.getImageFromUrl(image)
        }
    }

    private fun initPagerAndTab() {
        val fragments = mutableListOf<Fragment>(
            GridPostFragment(),
            ListPostFragment()
        )
        postPagerAdapter = PostPagerAdapter(fragments, childFragmentManager, lifecycle)
        binding.vpPostPager.adapter = postPagerAdapter
        TabLayoutMediator(binding.tlPostTab, binding.vpPostPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(drawable.ic_grid)
                1 -> tab.setIcon(drawable.ic_list)
            }
        }.attach()
    }
}