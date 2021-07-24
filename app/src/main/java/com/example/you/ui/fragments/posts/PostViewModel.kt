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
    private val repository: PostRepositoryImp,
) : ViewModel() {

    private val _posts by lazy {
        MutableLiveData<Resource<List<Post>>>()
    }
    val posts: LiveData<Resource<List<Post>>> = _posts
    private val _addComment by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val addComment: LiveData<Resource<Any>> = _addComment
    private val _postLikes by lazy {
        MutableLiveData<Resource<Boolean>>()
    }
    val postLikes: LiveData<Resource<Boolean>> = _postLikes


    fun getPostLikes(post: Post) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _postLikes.postValue(repository.getPostLikes(post))
        }
    }

    fun addComment(postId: String, text: String) = viewModelScope.launch {
        _addComment.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _addComment.postValue(Resource.Success(repository.addComment(postId, text)))
        }
    }

    fun getPosts() = viewModelScope.launch {
        _posts.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _posts.postValue(repository.getAllPost())
        }
    }
}