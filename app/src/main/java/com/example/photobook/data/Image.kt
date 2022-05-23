package com.example.photobook.data

import androidx.room.ColumnInfo
import androidx.room.Entity

import androidx.room.PrimaryKey


@Entity(tableName = "image")
data class Image (
    @PrimaryKey
    val name: String,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (name != other.name) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}