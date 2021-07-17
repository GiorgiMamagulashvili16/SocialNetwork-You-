package com.example.you.ui.fragments.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.post.Post
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepositoryImp
) : ViewModel() {

    private val _posts by lazy {
        MutableLiveData<Resource<List<Post>>>()
    }
    val posts: LiveData<Resource<List<Post>>> = _posts
    fun getPosts() = viewModelScope.launch {
        _posts.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _posts.postValue(repository.getAllPost())
        }
    }
}