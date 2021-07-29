package com.example.you.util

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.Provides
import retrofit2.HttpException

class ResponseHandler {
    fun <T> handleException(e: FirebaseFirestoreException, data: T? = null): Resource<T> {
        return when (e) {
            is HttpException -> Resource.Error("Http Exception", data)
            is NullPointerException -> Resource.Error("Null pointer exception", data)
            else -> Resource.Error("Unknown exception", data)
        }
    }

    fun <T> handleSuccess(data: T): Resource<T> {
        return Resource.Success(data)
    }

    fun <T> handleDefaultException(data: T? = null): Resource<T> {
        return Resource.Error("Unknown Exception!", data)
    }

    fun <T> handleLoading(): Resource<T> {
        return Resource.Loading()
    }
}