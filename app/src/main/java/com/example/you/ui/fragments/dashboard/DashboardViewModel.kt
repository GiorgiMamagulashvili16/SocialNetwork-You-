package com.example.you.ui.fragments.dashboard

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
class DashboardViewModel @Inject constructor(
    private val repository: PostRepositoryImp
) : ViewModel() {

    private val _curUser by lazy {
        MutableLiveData<Resource<UserModel>>()
    }
    val curUser: LiveData<Resource<UserModel>> = _curUser

    fun getUser(uid: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _curUser.postValue(repository.getUser(uid))
        }
    }
}