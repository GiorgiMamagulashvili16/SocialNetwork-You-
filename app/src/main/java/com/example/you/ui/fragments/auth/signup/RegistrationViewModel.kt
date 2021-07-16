package com.example.you.ui.fragments.auth.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.repositories.auth.AuthRepositoryImpl
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    private val _signUpResponse = MutableLiveData<Resource<AuthResult>>()
    val signUpResponse: LiveData<Resource<AuthResult>> = _signUpResponse

    fun signUp(
        email: String,
        password: String,
        userName: String,
        lat: Number,
        long: Number,
        imageUri: Uri
    ) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _signUpResponse.postValue(
                    repository.register(
                        email,
                        password,
                        userName,
                        lat,
                        long,
                        imageUri
                    )
                )
            }
        }
}