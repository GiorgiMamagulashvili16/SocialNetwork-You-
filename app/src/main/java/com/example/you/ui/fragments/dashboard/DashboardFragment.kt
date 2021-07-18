package com.example.you.ui.fragments.dashboard

import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.R
import com.example.you.adapters.DrawerAdapter
import com.example.you.databinding.DashboardFragmentBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.drawer.DrawerItem
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.addpost.AddPostFragment
import com.example.you.ui.fragments.my_profile.ProfileFragment
import com.example.you.ui.fragments.posts.PostFragment
import com.example.you.ui.fragments.radius.RadiusFragment
import com.example.you.ui.fragments.search.SearchFragment
import com.example.you.util.Constants.DEFAULT_DRAWER_ITEM
import com.example.you.util.Constants.DRAWER_LOG_OUT_INDEX
import com.example.you.util.Constants.UNDERLINED_DRAWER_ITEM
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

typealias drawable = R.drawable
typealias string = R.string

@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<DashboardFragmentBinding>(DashboardFragmentBinding::inflate) {
    private val auth = FirebaseAuth.getInstance()
    private val viewModel: DashboardViewModel by viewModels()
    private val drawerAdapter: DrawerAdapter by lazy { DrawerAdapter() }
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        initToolbar()
        initDrawer()
        setListeners()
        observeCurrentUser()
        viewModel.getUser(auth.uid!!)
    }

    private fun initToolbar() {

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowCustomEnabled(true)
            setSupportActionBar(binding.toolbar)
            supportActionBar?.title = null
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    private fun observeCurrentUser() {
        viewModel.curUser.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    binding.apply {
                        ivToolbarProfile.getShapeableImage(it.data!!.profileImageUrl)
                        tvToolbarUserName.text = it.data.userName
                    }
                }
                is Resource.Error -> d("CURRENTUSERERROR", "${it.errorMessage}")
                else -> Unit
            }
        })
    }

    private fun setListeners() {
        binding.apply {
            btnOpenDrawer.setOnClickListener {
                binding.root.openDrawer(GravityCompat.START)
            }
            ivToolbarProfile.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment2)
            }
        }
    }

    private fun initDrawer() {
        binding.rvDrawer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = drawerAdapter
        }
        drawerAdapter.setData(
            mutableListOf(
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    PostFragment(),
                    drawable.ic_home,
                    getString(string.news_feed)
                ),
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    RadiusFragment(),
                    drawable.ic_map,
                    getString(string.nearby_posts)
                ),
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    SearchFragment(),
                    drawable.ic_search,
                    getString(string.search)
                ),
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    ProfileFragment(),
                    drawable.ic_profile,
                    getString(string.profile)
                ),
                DrawerItem(
                    UNDERLINED_DRAWER_ITEM,
                    AddPostFragment(),
                    drawable.ic_add,
                    getString(string.add_post)
                ),
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    null,
                    drawable.ic_logout,
                    getString(string.log_out)
                )
            )
        )
        drawerAdapter.onMenuClick = {
            it?.let {
                if (it.icon == drawable.ic_profile)
                    findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment2)
                else
                    it.fragmentId?.let { fragment -> setCurrentFragment(fragment) }
            }
        }
        drawerAdapter.onLogOutClick = {

            if (it == DRAWER_LOG_OUT_INDEX) {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.action_global_logInFragment)
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.dashNavHostFragment, fragment)
            commit()
        }
    }


}