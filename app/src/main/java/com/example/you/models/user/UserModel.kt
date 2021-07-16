package com.example.you.models.user

import com.example.you.util.Constants.DEFAULT_PROFILE_IMAGE_URL

data class UserModel(
    val uid: String = "",
    val userName: String = "",
    val description: String = "",
    val lat: Number = 0.0,
    val long: Number = 0.0,
    val profileImageUrl: String = DEFAULT_PROFILE_IMAGE_URL
)