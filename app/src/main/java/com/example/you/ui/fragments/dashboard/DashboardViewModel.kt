package com.example.you.ui.fragments.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.you.repositories.posts.PostRepositoryImp
import com.example.you.util.Constants.USER_COLLECTION_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val userCollection = fireStore.collection(USER_COLLECTION_NAME)
    private val _curUserImage by lazy {
        MutableLiveData<String>()
    }
    val curUserImage: LiveData<String> = _curUserImage
    private val _curUserUserName by lazy {
        MutableLiveData<String>()
    }
    val curUserUserName: LiveData<String> = _curUserUserName

    fun getUser() = viewModelScope.launch {
        userCollection.document(auth.currentUser?.uid!!).addSnapshotListener { value, error ->
            error?.let {
                _curUserImage.postValue(it.toString())
                return@addSnapshotListener
            }
            value?.let { user ->
                _curUserImage.postValue(user["profileImageUrl"] as String)
                _curUserUserName.postValue(user["userName"] as String)
            }
        }
    }
}