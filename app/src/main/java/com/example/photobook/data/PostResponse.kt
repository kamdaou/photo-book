package com.example.photobook.data

/**
 * PostResponse - Represents response of a remote task
 *
 * @post: Value of the post if success or null
 * @exception: value of the exception if failure or null
 */
data class PostResponse(
    var post: List<PostFirestore>? = null,
    var exception: Exception? = null
)
