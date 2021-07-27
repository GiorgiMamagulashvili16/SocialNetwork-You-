package com.example.you.ui.fragments.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.DashboardGraphDirections
import com.example.you.R
import com.example.you.adapters.drawer.DrawerAdapter
import com.example.you.databinding.DashboardFragmentBinding
import com.example.you.extensions.getShapeableImage
import com.example.you.models.drawer.DrawerItem
import com.example.you.ui.base.BaseFragment
import com.example.you.util.Constants.DEFAULT_DRAWER_ITEM
import com.example.you.util.Constants.DRAWER_LOG_OUT_INDEX
import com.example.you.util.Constants.UNDERLINED_DRAWER_ITEM
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

typealias drawable = R.drawable
typealias string = R.string

@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<DashboardFragmentBinding>(DashboardFragmentBinding::inflate) {
    private val auth = FirebaseAuth.getInstance()
    private val viewModel: DashboardViewModel by viewModels()
    private val drawerAdapter: DrawerAdapter by lazy { DrawerAdapter() }

    private lateinit var navController: NavController

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        initToolbar()
        initDrawer()
        setListeners()
        observeCurUser()
        lifecycleScope.launch {
            viewModel.getUser()
        }
        val host =
            childFragmentManager.findFragmentById(R.id.dashNavHostFragment) as NavHostFragment
        navController = host.findNavController()
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

    private fun observeCurUser() {
        viewModel.curUserImage.observe(viewLifecycleOwner, {
            binding.ivToolbarProfile.getShapeableImage(it)
        })
        viewModel.curUserUserName.observe(viewLifecycleOwner, {
            binding.tvToolbarUserName.text = it
        })
    }


    private fun setListeners() {
        binding.apply {
            btnOpenDrawer.setOnClickListener {
                binding.root.openDrawer(GravityCompat.START)
            }
            ivToolbarProfile.setOnClickListener {
                val action = DashboardGraphDirections.actionGlobalProfileFragment()
                navController.navigate(action)
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
                    DashboardGraphDirections.actionGlobalPostFragment(),
                    drawable.ic_home,
                    getString(string.news_feed)
                ),
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    DashboardGraphDirections.actionGlobalRadiusFragment(),
                    drawable.ic_map,
                    getString(string.nearby_posts)
                ),
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    DashboardGraphDirections.actionGlobalSearchFragment(),
                    drawable.ic_search,
                    getString(string.search)
                ),
                DrawerItem(
                    DEFAULT_DRAWER_ITEM,
                    DashboardGraphDirections.actionGlobalProfileFragment(),
                    drawable.ic_profile,
                    getString(string.profile)
                ),
                DrawerItem(
                    UNDERLINED_DRAWER_ITEM,
                    DashboardGraphDirections.actionGlobalAddPostFragment2(),
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
                it.action?.let { action -> navController.navigate(action) }
            }
        }
        drawerAdapter.onLogOutClick = {
            if (it == DRAWER_LOG_OUT_INDEX) {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.action_global_logInFragment)
            }
        }
    }
}