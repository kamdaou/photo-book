package com.example.photobook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * CommentVote - Represents votes received by comments,
 * it's an association table
 *
 * @id: id of the element
 * @comment_id: id of the comment voted
 * @user_id: id of the user that voted
 * @score: score of the vote (-1, 0, 1)
 */
@Parcelize
data class CommentVote(
    val id: String = "",

    val comment_id: String="",

    val user_id: String="",

    val score: Float
): Parcelable
