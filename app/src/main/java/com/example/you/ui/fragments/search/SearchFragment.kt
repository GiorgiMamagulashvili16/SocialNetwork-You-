package com.example.you.ui.fragments.search

import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.user.UserAdapter
import com.example.you.databinding.SearchFragmentBinding
import com.example.you.ui.base.BaseFragment
import com.example.you.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchFragmentBinding>(SearchFragmentBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private val userAdapter: UserAdapter by lazy { UserAdapter() }
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        searchUser()
        initRec()
        observeSearchUser()
    }

    private fun initRec() {
        binding.rvSearchUser.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
        userAdapter.onUserClick = {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToOtherUserProfileFragment(
                    it
                )
            )
        }
    }

    private fun observeSearchUser() {
        viewModel.searchUser.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    d("HELLOHELO", "${it.data}")
                    userAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    it.errorMessage?.let { it1 -> showErrorDialog(it1) }
                }
                is Resource.Loading -> Unit
            }
        })
    }

    private fun searchUser() {
        var job: Job? = null

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(500)
                text?.let {
                    if (text.isNotEmpty()) {
                        viewModel.searchUser(text.toString())
                        binding.rvSearchUser.isVisible = true
                        binding.tvEmpty.isVisible = false
                    } else {
                        binding.tvEmpty.isVisible = true
                        binding.rvSearchUser.isVisible = false
                    }
                }
            }
        }
    }
}