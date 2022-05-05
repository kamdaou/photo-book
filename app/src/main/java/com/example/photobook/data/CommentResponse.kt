package com.example.photobook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * CommentResponse - Data retrieved from remote data source
 *
 * @comment: Value of comments if success or null
 * @exception: value of exception if failure or null
 */
@Parcelize
data class CommentResponse (
    var comment: List<Comment>? = null,
    var exception: Exception? = null
): Parcelable
