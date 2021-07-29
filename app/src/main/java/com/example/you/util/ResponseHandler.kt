package com.example.you.util

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class ResponseHandler {
    fun <T> handleException(e: Exception): Resource<T> {
        return when (e) {
            is FirebaseAuthInvalidUserException -> Resource.Error("User Information Is Not Correct or The User May Have Been Deleted")
            is FirebaseAuthInvalidCredentialsException -> Resource.Error("Email Address is badly formatted")
            is FirebaseAuthWeakPasswordException -> Resource.Error("Password Is Less Than 6 chars")
            is FirebaseAuthUserCollisionException -> Resource.Error("This Email is already used")
            else -> Resource.Error("Unknown exception")
        }
    }

    fun <T> handleSuccess(data: T): Resource<T> {
        return Resource.Success(data)
    }
}