package com.example.you.ui.fragments.chat

import android.graphics.Color
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.you.adapters.chat.ChatAdapter
import com.example.you.databinding.ChatFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.hide
import com.example.you.extensions.show
import com.example.you.models.message.Message
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Constants
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChatFragment : BaseFragment<ChatFragmentBinding>(ChatFragmentBinding::inflate) {
    private val viewModel: ChatViewModel by viewModels()
    private val chatAdapter: ChatAdapter by lazy { ChatAdapter() }
    private val args: ChatFragmentArgs by navArgs()

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        setListeners()
        initRec()
        observeSendResponse()
        observeReadMessage()

        val arg = arguments?.let {
            val safeArgs = ChatFragmentArgs.fromBundle(it)
            safeArgs.receiverId
        }
        lifecycleScope.launch {
            arg?.let {
                viewModel.readMessages(it)
            }
        }
        lifecycleScope.launch {
            viewModel.readMessages(args.receiverId)
        }
    }

    private fun setListeners() {
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun observeReadMessage() {
        viewModel.readMessages.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    d("SdSAD","${it.data}")
                    binding.progressBar.hide()
                    chatAdapter.differ.submitList(it.data)
                    binding.rvChat.scrollToPosition(chatAdapter.differ.currentList.size -1 )
                }
                is Resource.Error -> {
                    binding.progressBar.hide()
                    it.errorMessage?.let { errorMessage -> showErrorDialog(errorMessage) }
                }
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
            }
        })
    }

    private fun observeSendResponse() {
        viewModel.sendResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    binding.etMessage.setText("")
                    viewModel.readMessages(args.receiverId)
                }
                is Resource.Error -> {
                    binding.progressBar.hide()
                    it.errorMessage?.let { errorMessage -> showErrorDialog(errorMessage) }
                }
                is Resource.Loading -> {
                    binding.progressBar.show()
                }
            }
        })
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString()
        if (message.isBlank()) {
            createInfoSnackBar(getString(string.please_fill_all_fields), Color.RED)
        } else {
            viewModel.sendMessage(args.receiverId, message)
        }
    }

    private fun initRec() {
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }
}