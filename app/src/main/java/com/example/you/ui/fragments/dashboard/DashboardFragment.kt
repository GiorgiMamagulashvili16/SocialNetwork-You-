package com.example.you.ui.fragments.dashboard

import android.Manifest
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.example.you.util.ConnectionLiveData
import com.example.you.util.Constants.DEFAULT_DRAWER_ITEM
import com.example.you.util.Constants.DRAWER_LOG_OUT_INDEX
import com.example.you.util.Constants.UNDERLINED_DRAWER_ITEM
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

typealias drawable = R.drawable
typealias string = R.string

@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<DashboardFragmentBinding>(DashboardFragmentBinding::inflate) {
    private val viewModel: DashboardViewModel by viewModels()
    private val drawerAdapter: DrawerAdapter by lazy { DrawerAdapter() }
    private var isInternetConnection = true

    private lateinit var navController: NavController

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        observeInternetConnection()
        locationPermissionsRequest()
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

    private fun observeInternetConnection() {
        ConnectionLiveData(requireContext()).observe(viewLifecycleOwner, {
            isInternetConnection = it
        })
    }

    private fun observeCurUser() {
        viewModel.curUserImage.observe(viewLifecycleOwner, {
            binding.ivToolbarProfile.getShapeableImage(it)
        })
        viewModel.curUserUserName.observe(viewLifecycleOwner, {
            binding.tvToolbarUserName.text = it
        })
    }

    private fun locationPermissionsRequest() {
        when {
            hasFineLocationPermission() && hasCoarseLocationPermission() -> {
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Snackbar.make(
                    binding.root,
                    getString(string.app_needs_this_permission),
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(getString(string.ok)) {
                        requestLocationPermissions(permissionsLauncher)
                    }
                }.show()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                Snackbar.make(
                    binding.root,
                    getString(string.app_needs_this_permission),
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(getString(string.ok)) {
                        requestLocationPermissions(permissionsLauncher)
                    }
                }.show()
            }
            else -> requestLocationPermissions(permissionsLauncher)
        }
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
            tvToolbarUserName.setOnClickListener {
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
                    DEFAULT_DRAWER_ITEM,
                    DashboardGraphDirections.actionGlobalAddPostFragment2(),
                    drawable.ic_add,
                    getString(string.add_post)
                ),
                DrawerItem(
                    UNDERLINED_DRAWER_ITEM,
                    DashboardGraphDirections.actionGlobalNewsFragment(),
                    drawable.ic_leadboard,
                    getString(string.news)
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
            if (isInternetConnection) {
                it?.let {
                    it.action?.let { action -> navController.navigate(action) }
                    binding.root.closeDrawer(GravityCompat.START)
                }
            } else {
                showErrorDialog("No Internet Connection")
            }

        }
        drawerAdapter.onLogOutClick = {
            d("indindind", "$it")
            if (it == DRAWER_LOG_OUT_INDEX) {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.action_global_logInFragment)
            }
        }
    }

}