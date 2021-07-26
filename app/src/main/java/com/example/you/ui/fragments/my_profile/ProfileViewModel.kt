package com.example.you.ui.fragments.my_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.repositories.userProfile.UserProfileRepoImpl
import com.example.you.util.Constants
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserProfileRepoImpl,
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val postRepository: PostRepositoryImp
) : ViewModel() {
    private val _posts by lazy {
        MutableLiveData<Resource<List<Post>>>()
    }
    val posts: LiveData<Resource<List<Post>>> = _posts
    private val _user by lazy {
        MutableLiveData<Resource<UserModel>>()
    }
    val user: LiveData<Resource<UserModel>> = _user

    private val _postListSize by lazy {
        MutableLiveData<Int>()
    }
    val postListSize: LiveData<Int> = _postListSize

    private val _deleteCommentByPost by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val deleteCommentsByPost: LiveData<Resource<Any>> = _deleteCommentByPost

    fun deleteCommentsByPost(postId: String) = viewModelScope.launch {
        _deleteCommentByPost.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _deleteCommentByPost.postValue(repository.deleteCommentByPostId(postId))
        }
    }

    private val _deletePostResponse by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val deletePostResponse: LiveData<Resource<Any>> = _deletePostResponse
    fun deletePost(postId: String) = viewModelScope.launch {
        _deletePostResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _deletePostResponse.postValue(postRepository.deletePost(postId))
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        _user.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _user.postValue(repository.getUser(auth.uid!!))
        }
    }

    fun getPosts() = viewModelScope.launch {
        _posts.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _posts.postValue(repository.getUserProfilePosts(auth.uid!!))
            _postListSize.postValue(repository.getUserProfilePosts(auth.uid!!).data?.size)
        }
    }
}