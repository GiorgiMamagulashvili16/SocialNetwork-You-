package com.example.you.models.user

import com.example.you.util.Constants.DEFAULT_PROFILE_IMAGE_URL

data class User(
    val uid:String = "",
    val userName:String = "",
    val description:String = "",
    val profilePictureUrl:String = DEFAULT_PROFILE_IMAGE_URL,
)
