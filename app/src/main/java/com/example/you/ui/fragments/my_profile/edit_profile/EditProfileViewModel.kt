package com.example.you.ui.fragments.my_profile.edit_profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.models.user.ProfileUpdate
import com.example.you.models.user.UserModel
import com.example.you.repositories.userProfile.UserProfileRepoImpl
import com.example.you.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: UserProfileRepoImpl
) : ViewModel() {
    private val _editUserInfo by lazy {
        MutableLiveData<Resource<Any>>()
    }
    val editUserInfo: LiveData<Resource<Any>> = _editUserInfo

    private val _userInfo by lazy {
        MutableLiveData<Resource<UserModel>>()
    }
    val userInfo: LiveData<Resource<UserModel>> = _userInfo

    private val _profileImage by lazy {
        MutableLiveData<Uri>()
    }
    val profileImage: LiveData<Uri> = _profileImage
    fun getCurrentImage(uri: Uri) = viewModelScope.launch {
        _profileImage.postValue(uri)
    }

    fun editUserInfo(
        profileUpdate: ProfileUpdate
    ) =
        viewModelScope.launch {
            _editUserInfo.postValue(Resource.Loading())
            withContext(Dispatchers.IO) {
                _editUserInfo.postValue(
                    repository.updateProfile(
                        profileUpdate
                    )
                )
            }
        }

    fun getUser() = viewModelScope.launch {
        _userInfo.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _userInfo.postValue(repository.getUser(auth.uid!!))
        }
    }
}