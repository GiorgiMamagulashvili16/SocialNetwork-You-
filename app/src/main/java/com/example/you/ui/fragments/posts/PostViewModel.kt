package com.example.you.ui.fragments.posts

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
import com.example.you.paging_source.AllPostSource
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.util.Constants.POST_PAGE_SIZE
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
class PostViewModel @Inject constructor(
    private val repository: PostRepositoryImp,
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _addComment by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val addComment: LiveData<Resource<Any>> = _addComment
    private val _postLikes by lazy {
        MutableLiveData<Resource<Boolean>>()
    }
    val postLikes: LiveData<Resource<Boolean>> = _postLikes

    fun getAllPost(): Flow<PagingData<Post>> {
        val pagingSource = AllPostSource(
            fireStore, auth
        )
        return Pager(PagingConfig(POST_PAGE_SIZE)) {
            pagingSource
        }.flow.cachedIn(viewModelScope)
    }

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

}