package com.example.photobook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * User - Represents user
 *
 * @username: The display name of the user in firebase
 * @id: His id
 */
@Parcelize
data class User(
    var username: String = "",

    var id: String = ""
):Parcelable
