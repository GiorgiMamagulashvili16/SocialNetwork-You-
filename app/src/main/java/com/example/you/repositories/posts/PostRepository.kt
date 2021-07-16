package com.example.you.repositories.posts

import android.net.Uri
import com.example.you.util.Resource

interface PostRepository {

    suspend fun addPost(imageUrl:Uri,postText:String)

}