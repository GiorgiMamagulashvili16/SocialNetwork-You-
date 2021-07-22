package com.example.you.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.user.UserModel
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PostRepositoryImp
) : ViewModel() {
    private val _searchUser by lazy {
        MutableLiveData<Resource<List<UserModel>>>()
    }
    val searchUser: LiveData<Resource<List<UserModel>>> = _searchUser

    fun searchUser(query:String) = viewModelScope.launch {
        _searchUser.postValue(Resource.Loading())
        withContext(Dispatchers.IO){
            _searchUser.postValue(repository.searchUser(query))
        }
    }
}