package com.example.you.ui.fragments.bottom_sheets

import android.app.Dialog
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.comment.CommentAdapter
import com.example.you.databinding.BottomSheetCommentsBinding
import com.example.you.databinding.DialogDeletePostBinding
import com.example.you.extensions.setDialog
import com.example.you.util.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetComments : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCommentsBinding? = null
    private val binding get() = _binding!!

    private val commentAdapter: CommentAdapter by lazy { CommentAdapter() }
    private val viewModel: CommentsViewModel by viewModels()
    private var deleteCommentDialog: Dialog? = null

    val args: BottomSheetCommentsArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCommentsBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel.getComments(args.postId)
        observeDeleteCommentResponse()
        observeComments()
        initRecycleView()
    }

    private fun observeComments() {
        viewModel.comments.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.isEmpty())
                        binding.tvNoComment.isVisible = true
                    else
                        commentAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    d("commentObserveError", "${it.errorMessage}")
                }
                is Resource.Loading -> Unit

            }
        })
    }

    private fun initRecycleView() {
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
        commentAdapter.onDeleteClick = {
            showDeleteDialog(it)
        }
    }

    private fun observeDeleteCommentResponse() {
        viewModel.deleteComment.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    deleteCommentDialog!!.dismiss()
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    deleteCommentDialog!!.dismiss()
                }
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun showDeleteDialog(commentId: String) {
        deleteCommentDialog = Dialog(requireContext())
        val binding = DialogDeletePostBinding.inflate(layoutInflater)
        deleteCommentDialog!!.setDialog(binding)
        binding.apply {
            btnYes.setOnClickListener {
                viewModel.deleteComment(commentId)
            }
            btnNo.setOnClickListener {
                deleteCommentDialog!!.dismiss()
            }
        }
        deleteCommentDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}