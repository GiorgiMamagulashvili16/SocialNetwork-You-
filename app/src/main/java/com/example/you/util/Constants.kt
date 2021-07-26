package com.example.you.util

object Constants {

    const val DEFAULT_PROFILE_IMAGE_URL =
        "https://firebasestorage.googleapis.com/v0/b/finalproject-you-79a85.appspot.com/o/pngegg.png?alt=media&token=3d53fb88-7d9d-4091-a31d-0e45792d3292"

    const val DEFAULT_DRAWER_ITEM = 0
    const val UNDERLINED_DRAWER_ITEM = 1

    const val USER_COLLECTION_NAME = "users"
    const val POSTS_COLLECTION_NAME = "posts"
    const val COMMENTS_COLLECTION_NAME = "comments"
    const val CHAT_COLLECTION_NAME = "chat"
    const val AUTHOR_ID_FIELD=  "authorId"
    const val POST_TYPE_FIELD = "postType"
    const val TIME_FIELD = "time"

    const val DRAWER_LOG_OUT_INDEX = 5

    const val POST_PAGE_SIZE = 5

    const val CURRENT_USER_LOCATION = "current_user_location"
    const val OTHER_USER_LOCATION = "other_user_location"
    const val DISTANCE_RADIUS = 5000.0

    const val POST_TYPE_FOR_RADIUS = "for_radius"
    const val POST_TYPE_FOR_ALL = "for_all"
}