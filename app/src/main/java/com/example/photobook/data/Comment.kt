package com.example.photobook.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

/**
 * Comment - Comment posted by users
 *
 * @id: Id of the element
 * @user_id: id of the user that posted the comment
 * @inserted_at: time when the comment has been inserted
 * @parent_comment: Id of the comment to which
 * this one is replying, if any
 * @body: The value of the comment
 * @post_id: id of the post to which the comment belongs
 * @level: level of the comment (0, 1, 2). This will allow
 * to well display the comment in the UI
 */
@Parcelize
data class Comment(
    var id: String = "",

    var user_id: String="",

    var inserted_at: Timestamp = Timestamp.now(),

    var parent_comment: String="",

    var body: String="",

    var post_id:String="",

    var level:Int = 0
): Parcelable
