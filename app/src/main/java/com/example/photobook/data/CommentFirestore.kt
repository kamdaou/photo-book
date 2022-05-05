package com.example.photobook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * CommentFirestore - Structure of the comment when saved in
 * firestore database
 *
 * @comment: The comment.
 * @comment_vote: List of vote that the comment has received
 */
@Parcelize
data class CommentFirestore (
    var comment:Comment = Comment(),
    var comment_vote: MutableList<CommentVote> = mutableListOf()
): Parcelable
