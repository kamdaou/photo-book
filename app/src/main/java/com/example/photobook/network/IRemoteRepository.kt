package com.example.photobook.network

import com.example.photobook.data.*
import com.example.photobook.utils.VoteType
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User

interface IRemoteRepository {
    val db: FirebaseFirestore

    /**
     * saveMedia - Saves a media in firestore database
     *
     * @media: The media that will be saved
     *
     * Return: The result of the operation, id of the
     * saved data and the task.
     */
    fun saveMedia(media: Media): Result

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
    suspend fun savePostMedia(post: Post, media: Media, user: com.example.photobook.data.User): Result

    /**
     * savePost - Saves a post, without any media.
     *
     * @post: Post that should be saved
     * @user: user that created the post
     *
     * Return: Result of the operation
     */
    suspend fun savePost(post: Post, user: com.example.photobook.data.User): Result

    /**
     * getPosts - Reads posts from remote data source
     *
     * @limit: Maximal amount of post that should be read
     *
     * Return: a PostResponse
     */
    suspend fun getPosts(limit: Long): PostResponse

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
     * updateComment - updates value of comment.
     *
     * @comment: comment that might be updated
     * @field: field to update
     * @value: New value of the field
     *
     * Return: Google task.
     */
    suspend fun updateComment(comment: Comment, field: String, value: Any): Task<Void>?

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
     * saveComment - Saves a comment
     *
     * @comment - Comment to save
     *
     * Return: Result of the operation if the comment refers to a post or null
     */
    suspend fun saveComment(comment: Comment): Result?

    /**
     * getComments - Retrieves comments from remote data source
     *
     * @postId: Id of the post to which comments belong
     *
     * Return: CommentResponse element
     */
    suspend fun getComments(postId: String): CommentResponse

    /**
     * getCommentSnap - Retreives a comment from firestore
     * @id: id of the comment to retrieve
     *
     * Return: Google task
     */
    suspend fun getCommentSnap(id: String): Task<QuerySnapshot>

    /**
     * getComment - Gets comment from firestore
     *
     * @comment: Comment to get, only the id will be used
     *
     * Return: The comment or null
     */
    suspend fun getComment(comment: Comment): Comment?

    /**
     * deleteAllPosts - Deletes 1000000 from data source
     */
    suspend fun deleteAllPosts()
}