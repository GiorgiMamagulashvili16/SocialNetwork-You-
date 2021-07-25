package com.example.you.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.you.models.post.Post
import com.example.you.models.user.UserModel
import com.example.you.util.Constants
import com.example.you.util.Constants.AUTHOR_ID_FIELD
import com.example.you.util.Constants.POSTS_COLLECTION_NAME
import com.example.you.util.Constants.POST_TYPE_FIELD
import com.example.you.util.Constants.POST_TYPE_FOR_RADIUS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class AllPostSource(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PagingSource<QuerySnapshot, Post>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val uid = auth.currentUser?.uid!!
            val result = mutableListOf<Post>()

            val currentPage = params.key ?: fireStore.collection(POSTS_COLLECTION_NAME)
                .whereNotEqualTo(POST_TYPE_FIELD, POST_TYPE_FOR_RADIUS)
                .get()
                .await()
            val lastDocument = currentPage.documents[currentPage.size() - 1]

            val nextPage = fireStore.collection(POSTS_COLLECTION_NAME)
                .whereNotEqualTo(POST_TYPE_FIELD, POST_TYPE_FOR_RADIUS)
                .startAfter(lastDocument)
                .get()
                .await()

            currentPage.toObjects(Post::class.java).onEach { post ->
                val currentUser =
                    fireStore.collection(Constants.USER_COLLECTION_NAME).document(post.authorId)
                        .get()
                        .await()
                val currentUserModel = UserModel(
                    currentUser["uid"] as String,
                    currentUser["userName"] as String,
                    currentUser["description"] as String,
                    profileImageUrl = currentUser["profileImageUrl"] as String,
                )
                post.apply {
                    authorUserName = currentUserModel.userName
                    authorProfileImageUrl = currentUserModel.profileImageUrl
                    isLiked = uid in post.likedBy
                }
                if (post.authorId != uid)
                    result.add(post)
            }


            LoadResult.Page(
                result,
                null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}