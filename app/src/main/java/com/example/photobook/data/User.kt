package com.example.photobook.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * User - Represents user
 *
 * @username: The display name of the user in firebase
 * @id: His id
 */
@Parcelize
@Entity(tableName = "user")
data class User(
    var username: String = "",

    @PrimaryKey(autoGenerate=false)
    var id: String = ""
):Parcelable
