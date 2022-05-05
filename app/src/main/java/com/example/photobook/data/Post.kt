package com.example.photobook.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

/**
 * Post - data class to represent a post
 *
 * @id: Id of the post
 * @submitter_id: id of the user submitting the post
 * @title: Title of the post
 * @inserted_at: Time when the post has been submitted
 * @body: The body of the post
 * @city: The city from where the post has been done
 * @media_id: Id of a media, if any
 */
@Parcelize
data class Post (
    var id:String = "",

    var submitter_id: String,

    val inserted_at: Timestamp = Timestamp.now(),

    var title:String = "",

    var body:String = "",

    var city: String = "",

    var media_id: String = ""
): Parcelable
