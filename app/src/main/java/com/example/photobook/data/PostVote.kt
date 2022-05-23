package com.example.photobook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * PostVote - Vote received by a post.
 *
 * @id: id of the element
 * @user_id: id of the user that voted the post
 * @post_id: id of the voted post
 * @score: score of the vote (-1, 0, 1)
 */
@Parcelize
data class PostVote(
    val id: String = "",

    val user_id: String="",

    val post_id: String="",

    var score: Float = 0F
): Parcelable
