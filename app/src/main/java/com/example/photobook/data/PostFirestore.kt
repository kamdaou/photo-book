package com.example.photobook.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

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
