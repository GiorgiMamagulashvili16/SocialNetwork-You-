package com.example.you.ui.signupfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.repositories.auth.AuthRepositoryImplement
import com.example.you.util.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel : ViewModel() {
    private val repo: AuthRepositoryImplement by lazy { AuthRepositoryImplement() }
    val registerResponse: MutableLiveData<Resource<AuthResult>> = MutableLiveData()
    fun register(
        email: String,
        password: String,
        userName: String,
        status: String

    ) = viewModelScope.launch {
        registerResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            val response = repo.register(email, password, userName, status)
            registerResponse.postValue(response)
        }
    }
}