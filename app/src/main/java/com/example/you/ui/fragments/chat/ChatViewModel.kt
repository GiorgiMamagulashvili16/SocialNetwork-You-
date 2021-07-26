package com.example.you.ui.fragments.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.message.Message
import com.example.you.repositories.chat.ChatRepositoryImpl
import com.example.you.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepositoryImpl
) : ViewModel() {
    private val _sendResponse by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val sendResponse: LiveData<Resource<Any>> = _sendResponse

    private val _readResponse by lazy {
        MutableLiveData<Resource<List<Message>>>()
    }
    val readMessages: LiveData<Resource<List<Message>>> = _readResponse
    fun readMessages(receiverId: String) = viewModelScope.launch {
        _readResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _readResponse.postValue(chatRepository.readMessage(receiverId))
        }
    }

    fun sendMessage(receiverId: String, text: String) = viewModelScope.launch {
        _sendResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _sendResponse.postValue(chatRepository.sendMessage(receiverId, text))
        }
    }
}