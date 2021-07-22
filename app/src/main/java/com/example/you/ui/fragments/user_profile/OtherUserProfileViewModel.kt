package com.example.you.ui.fragments.user_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.repositories.userProfile.UserProfileRepoImpl
import com.example.you.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val repository: UserProfileRepoImpl,
    private val postRepo:PostRepositoryImp
) : ViewModel() {
    private val _user by lazy {
        MutableLiveData<Resource<UserModel>>()
    }
    val user: LiveData<Resource<UserModel>> = _user
    private val _posts by lazy {
        MutableLiveData<Resource<List<Post>>>()
    }
    val posts: LiveData<Resource<List<Post>>> = _posts

    private val _postLikes by lazy {
        MutableLiveData<Resource<Boolean>>()
    }
    val postLikes: LiveData<Resource<Boolean>> = _postLikes

    fun getPostLikes(post: Post) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _postLikes.postValue(postRepo.getPostLikes(post))
        }
    }

    fun getUser(uid: String) = viewModelScope.launch {
        _user.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _user.postValue(repository.getUser(uid))
        }
    }

    fun getUserPosts(authorId: String) = viewModelScope.launch {
        _posts.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _posts.postValue(repository.getUserPosts(authorId))
        }
    }
}