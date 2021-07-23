package com.example.you.ui.fragments.bottom_sheets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.post.Comment
import com.example.you.models.user.UserModel
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repository: PostRepositoryImp
) : ViewModel() {
    private val _comments by lazy {
        MutableLiveData<Resource<List<Comment>>>()
    }
    val comments: LiveData<Resource<List<Comment>>> = _comments
    private val _deleteComment by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val deleteComment: LiveData<Resource<Any>> = _deleteComment

    private val _likedByResponse by lazy {
        MutableLiveData<Resource<List<UserModel>>>()
    }
    val likedByResponse: LiveData<Resource<List<UserModel>>> = _likedByResponse
    fun getLikedBy(users: List<String>) = viewModelScope.launch {
        _likedByResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _likedByResponse.postValue(repository.getLikedByUsers(users))
        }
    }

    fun deleteComment(commentId: String) = viewModelScope.launch {
        _deleteComment.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _deleteComment.postValue(repository.deleteComment(commentId))
        }
    }

    fun getComments(postId: String) = viewModelScope.launch {
        _comments.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _comments.postValue(repository.getCommentForPost(postId))
        }
    }
}