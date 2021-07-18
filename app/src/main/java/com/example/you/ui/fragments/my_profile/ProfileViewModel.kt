package com.example.you.ui.fragments.my_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.repositories.userProfile.UserProfileRepoImpl
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserProfileRepoImpl,
    private val auth: FirebaseAuth
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