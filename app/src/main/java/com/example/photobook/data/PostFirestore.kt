package com.example.photobook.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * PostFirestore - Its the document that is retrieved from firestore
 *
 * @id: Id of the post
 * @submitter_id: id of the user submitting the post
 * @title: Title of the post
 * @inserted_at: Time when the post has been submitted
 * @body: The body of the post
 * @city: The city from where the post has been done
 * @media_id: Id of a media, if any
 * @post_vote: votes received by the post
 * @media: media (image or video) saved with the post, if any
 * @user: The user who saved the post
 */
@Parcelize
data class PostFirestore(
    val title: String="",

    val body: String="",

    val submitter_id: String="",

    val inserted_at: Timestamp?=null,

    val city: String="",

    var id: String="",

    var user: @RawValue User?=null,

    var media:Media?=null,

    val post_vote: MutableList<PostVote> = mutableListOf()

): Parcelable
