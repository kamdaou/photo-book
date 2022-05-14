package com.example.photobook.network

import android.net.Uri
import com.example.photobook.data.*
import com.example.photobook.utils.Constants.VoteType
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask

interface IRemoteRepository {

    /**
     * saveMedia - Saves a media in firestore database
     *
     * @media: The media that will be saved
     *
     * Return: The result of the operation, id of the
     * saved data and the task.
     */
    suspend fun saveMedia(media: Media): Result

    /**
     * getMedia - Gets media from firestore datasource.
     *
     * @id: Id of the media to be gotten.
     *
     * Return: The media if it exist or null otherwise.
     */
    suspend fun getMedia(id: String): Media?

    /**
     * savePostMedia - saves a post that contains a media,
     * either a video or an image.
     *
     * @post: The post to save
     * @media: The media to save
     * @user: The user that is saving the media
     *
     * Return: The result of the operation
     */
    suspend fun savePostMedia(post: Post, media: Media, user: User): Result

    /**
     * savePost - Saves a post, without any media.
     *
     * @post: Post that should be saved
     * @user: user that created the post
     *
     * Return: Result of the operation
     */
    suspend fun savePost(post: Post, user: User): Result

    /**
     * getPosts - Reads posts from remote data source
     *
     * @limit: Maximal amount of post that should be read
     *
     * Return: a PostResponse
     */
    suspend fun getPosts(limit: Long = 20L, lastSeen: PostFirestore?): PostResponse

    /**
     * updatePost - update value of a post.
     *
     * @post: Post that should be updated.
     * @field: The field of the post that will be updated.
     * @value: New value of the field.
     *
     * Return: Google task.
     */
    suspend fun updatePost(post: PostFirestore, field: String, value: Any): Task<Void>

    /**
     * getPostSnap: Returns a querySnapshot of post
     * from remote data source
     *
     * @id: id of the post.
     *
     * Return: QuerySnapshot
     */
    suspend fun getPostSnap(
        id: String
    ): QuerySnapshot

    /**
     * getPost - Returns a post from firestore
     *
     * @id: Id of the post to retrieve
     *
     * Return: a PostFirestore element if it exists or null.
     */
    suspend fun getPost(id: String): PostFirestore?

    /**
     * saveVote - Save vote in remote data source
     *
     * @frameworkData: Post voted
     * @voteType: Type of the vote, either UP or DOWN
     *
     * Return: result of the operation
     */
    suspend fun saveVote(
        frameworkData: PostFirestore,
        voteType: VoteType
    ): Result?

    /**
     * getPostVoteSnap - Gets a querySnapshot from remote data source
     *
     * @userId: id of the user to who the post belongs
     * @id: id of the post to retrieve
     *
     * Return - querySnapshot if the post exists or null otherwise
     */
    suspend fun getPostVoteSnap(
        userId: String,
        id: String
    ): QuerySnapshot?

    /**
     * getPostVote - Gets a vote
     *
     * @userId: id of the user that voted
     * @id: id of the post
     *
     * Return: PostVote element or null
     */
    suspend fun getPostVote(userId: String, id: String): PostVote?

    /**
     * deleteAllPosts - Deletes 1000000 from data source
     */
    suspend fun deleteAllPosts()

    /**
     * saveImage - Saves image in firebase database
     *
     * @imageName: The name of the image in firebase database
     * @data: A bit array that represents the image
     *
     * Return: An uploadTask.
     */
    suspend fun saveImage(imageName: String, data: ByteArray): UploadTask

    /**
     * saveVideo - Saves video in firebase database
     *
     * @videoName: The name of the video in firebase database
     * @videoUri: An uri that represents the video
     *
     * Return: An uploadTask.
     */
    suspend fun saveVideo(videoUri: Uri, videoName: String): UploadTask
}
