package com.example.you.ui.fragments.radius

import android.location.Location
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
class RadiusViewModel @Inject constructor(
    private val repository: PostRepositoryImp
) : ViewModel() {
    private val _nearbyPosts by lazy {
        MutableLiveData<Resource<List<Post>>>()
    }
    val nearbyPosts: LiveData<Resource<List<Post>>> = _nearbyPosts

    private val _postLikes by lazy {
        MutableLiveData<Resource<Boolean>>()
    }
    val postLikes: LiveData<Resource<Boolean>> = _postLikes

    fun getPostLikes(post: Post) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _postLikes.postValue(repository.getPostLikes(post))
        }
    }

    fun getNearbyPosts(location: Location) = viewModelScope.launch {
        _nearbyPosts.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _nearbyPosts.postValue(repository.getNearbyPosts(location))
        }
    }

}