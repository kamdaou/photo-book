package com.example.photobook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * LastSeen - Its the last document that is retrieved from firestore
 *
 * @id: Id of the post
 */
@Entity(tableName = "last_seen")
data class LastSeen(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val element_id: String
)
