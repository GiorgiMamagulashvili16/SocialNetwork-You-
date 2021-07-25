package com.example.you.ui.fragments.addpost

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val repository: PostRepositoryImp
) : ViewModel() {

    private val _addPostResponse by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val addPostResponse: LiveData<Resource<Any>> = _addPostResponse

    fun addPost(imageUri: Uri, postText: String,postType:String) = viewModelScope.launch {
        _addPostResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _addPostResponse.postValue(repository.addPost(imageUri, postText,postType))
        }
    }
}