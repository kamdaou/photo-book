package com.example.photobook.utils

import android.graphics.Bitmap
import androidx.room.TypeConverter
import com.example.photobook.data.Media
import com.example.photobook.data.Post
import com.example.photobook.data.PostFirestore
import com.example.photobook.data.User
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.util.*

class Converter
{
    /**
     * fromTimesTamp - convert a timestamp to date
     *
     * @value: the timestamp
     *
     * Return: date
     */
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Date?
    {
        return value?.toDate()
    }

    /**
     * dateToTimestamp - converts date to timestamp
     *
     * @date: the date that should be converted
     *
     * Return: a timestamp
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Timestamp?
    {
        return date?.let { Timestamp(it) }
    }

    /**
     * fromDateToLong - converts date to Long value
     *
     * @date - the date to be converted
     *
     * return: a long
     */
    @TypeConverter
    fun fromDateToLong(date: Date?): Long?
    {
        return date?.time
    }

    /**
     * fromLongToDate - converts a long value to date
     *
     * @long: the value that should be converted
     *
     * Return: a date
     */
    @TypeConverter
    fun fromLongToDate(long: Long?): Date?
    {
        return long?.let { Date(it) }
    }

    /**
     * listToString - uses Gson to convert a list to a gson string
     *
     * @list: list that should be converted
     *
     * Return: A string
     */
    @TypeConverter
    fun listToString(list: List<String>): String
    {
        return Gson().toJson(list)
    }

    /**
     * fromString - uses Gson to convert a string to a list of string
     *
     * @string: the string that should be converted
     *
     * Return: list of string
     */
    @TypeConverter
    fun fromString(string: String): List<String>
    {
        val type: Type = object: TypeToken<List<String>>() {}.type
        return Gson().fromJson(string, type)
    }

    /**
     * postFirestoreToPost - converts an instance of postFirestore class
     * to an instance of post
     *
     * @postFirestore: the element that should be converted
     *
     * Return: an instance of post class
     */
    fun postFirestoreToPost(postFirestore: PostFirestore): Post
    {
        return Post(
            id = postFirestore.id,
            submitter_id = postFirestore.submitter_id,
            inserted_at = postFirestore.inserted_at,
            city = postFirestore.city,
            body = postFirestore.body,
            title = postFirestore.title,
            media_id = if (postFirestore.media == null) "" else postFirestore.media!!.id
        )
    }

    /**
     * postToPostFirestore - converts instances of post, media and user
     * classes into an instance of postFirestore class
     *
     * @post: the post
     * @media: the media associated to the post
     * @user: the user, author of the post
     *
     * Return: an instance of postFirestore class
     */
    fun postToPostFirestore(post: Post, media: Media?, user: User?): PostFirestore
    {
        return PostFirestore(
            title = post.title,
            body = post.body,
            submitter_id = post.submitter_id,
            inserted_at = post.inserted_at,
            city = post.city,
            id = post.id,
            user = user,
            media = media,
            post_vote = mutableListOf()
        )
    }

    /**
     * bitmapToBitArray - converts a bitMap into bitArray
     *
     * @bitmap: the bitmap that should be converted
     *
     * Return: a bitArray
     */
    fun bitmapToBitArray(bitmap: Bitmap?): ByteArray {
        if (bitmap == null) {
            return ByteArray(10)
        }
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}
