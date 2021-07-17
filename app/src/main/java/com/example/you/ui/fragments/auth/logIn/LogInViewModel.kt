package com.example.you.ui.fragments.auth.logIn

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
class LogInViewModel @Inject constructor(
    private val repository: AuthRepositoryImpl
) : ViewModel() {
    private val _logInResponse by lazy {
        MutableLiveData<Resource<AuthResult>>()
    }
    val logInResponse: LiveData<Resource<AuthResult>> = _logInResponse
    fun logIn(email: String, password: String) = viewModelScope.launch {
        _logInResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            _logInResponse.postValue(repository.logIn(email, password))
        }
    }
}