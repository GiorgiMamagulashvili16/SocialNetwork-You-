package com.example.you.models.user

import android.net.Uri

import com.example.you.util.Constants.DEFAULT_PROFILE_IMAGE_URL

data class UserModel(
    val uid: String = "",
    val userName: String = "",
    val description: String = "",
    val profileImageUrl: String = DEFAULT_PROFILE_IMAGE_URL,
)
data class ProfileUpdate(
    val uid: String = "",
    val userName: String = "",
    val description: String = "",
    val profileImageUri: Uri? = null,
)