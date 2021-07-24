package com.example.you.ui.fragments.radius

import android.location.Location
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.you.models.post.Post
import com.example.you.paging_source.RadiusPostSource
import com.example.you.repositories.posts.PostRepositoryImp
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
class RadiusViewModel @Inject constructor(
    private val repository: PostRepositoryImp,
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _postLikes by lazy {
        MutableLiveData<Resource<Boolean>>()
    }
    val postLikes: LiveData<Resource<Boolean>> = _postLikes

    fun getNearbyPosts(location: Location): Flow<PagingData<Post>> {
        val pagingSource = RadiusPostSource(
            fireStore, auth, location
        )
        return Pager(PagingConfig(PAGE_SIZE)) {
            pagingSource
        }.flow.cachedIn(viewModelScope)
    }

    fun getPostLikes(post: Post) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _postLikes.postValue(repository.getPostLikes(post))
        }
    }


}