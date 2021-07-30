package com.example.you.ui.fragments.bottom_sheets

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.user.UserAdapter
import com.example.you.databinding.BottomSheetLikesBinding
import com.example.you.util.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetLikes : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLikesBinding? = null
    private val binding get() = _binding!!

   private val viewModel:CommentsViewModel by viewModels()
    val args: BottomSheetLikesArgs by navArgs()
    private val userAdapter: UserAdapter by lazy { UserAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLikesBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel.getLikedBy(args.uids.toList())
        observeLikedBy()
        initRec()
    }

    private fun observeLikedBy() {
        viewModel.likedByResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.isEmpty()) {
                        binding.tvNoLikes.isVisible = true
                    } else {
                        userAdapter.differ.submitList(it.data)
                    }
                }
                is Resource.Error -> {
                    d("likesRsponse", "${it.errorMessage}")
                }
                is Resource.Loading -> {

                }
            }
        })
    }

    private fun initRec() {
        binding.rvLikes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}