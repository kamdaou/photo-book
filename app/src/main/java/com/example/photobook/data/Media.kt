package com.example.photobook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Media - Couple of medias associated with a post,
 * either a video or an image
 *
 * @id: Id of the media
 * @valid: Indicates if the media is valid (or exist) or not
 * @media_type: Type of the media, Image or Video
 * @url: Url list of medias
 */
@Parcelize
data class Media(
    var id: String="",

    val valid: String = "",

    val media_type: String="",

    var title: String="",

    var url: List<String> = listOf()
):Parcelable
