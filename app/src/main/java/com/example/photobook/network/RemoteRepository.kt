package com.example.photobook.network

import android.util.Log
import com.example.photobook.data.*
import com.example.photobook.utils.Constants.VoteType
import com.example.photobook.utils.Login.currentUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

private const val TAG = "RemoteRepository"
/**
 * RemoteRepository - Allows interactions between users
 * by sending data to firebase database
 */
class RemoteRepository
    : IRemoteRepository
{
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    init
    {
        useEmulator()
    }

    /**
     * useEmulator - Uses firebase emulator to add data
     * this allows us to test the app without having
     * to be charged by firebase bill
     */
    private fun useEmulator()
    {
        if (!firestoreUsingEmulator)
        {
            try {
                db.useEmulator("10.0.2.2", 8080)
                firestoreUsingEmulator = true
            }
            catch (e: Exception) {
                Log.e(TAG, "unable to use emulator for firestore: ${e.message}")
            }
        }
        if (!storageUsingEmulator)
        {
            try {
                storage.useEmulator("10.0.2.2", 8080)
                storageUsingEmulator = true
            }
            catch (e: Exception) {
                Log.e(TAG, "unable to use emulator for storage: ${e.message}")
            }
        }
    }

    /**
     * saveMedia - Saves a media in firestore database
     *
     * @media: The media that will be saved
     *
     * Return: The result of the operation, id of the
     * saved data and the task.
     */
    override suspend fun saveMedia(media: Media): Result
    {
        // Getting media from Firestore
        val medias = db.collection("media").document()

        // Creating a map of media to save
        val mediaMap = hashMapOf(
            "title" to media.title,
            "url" to media.url,
            "media_type" to media.media_type,
            "valid" to media.valid
        )
        mediaMap["id"] = if (media.id == "")
        {
            medias.id
        }
        else
        {
            media.id
        }

        // Saving the new media
        return Result(medias.id, medias.set(mediaMap))
    }

    /**
     * getMedia - Gets media from firestore datasource.
     *
     * @id: Id of the media to be gotten.
     *
     * Return: The media if it exist or null otherwise.
     */
    override suspend fun getMedia(id: String): Media?
    {
        var media: Media? = null
        db.collection("media")
            .whereEqualTo("id", id)
            .get()
            .await()
            .documents.mapNotNull { documentSnapshot ->
                media = documentSnapshot.toObject(Media::class.java)
            }
        return media
    }

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
    override suspend fun savePostMedia(post: Post, media: Media, user: User): Result
    {
        val postsMedia = db.collection("post").document()
        val mediaMap = hashMapOf(
            "title" to media.title,
            "url" to media.url,
            "media_type" to media.media_type,
            "id" to media.id,
            "valid" to media.valid
        )
        // Creating a map of to save
        val postMap = hashMapOf(
            "title" to post.title,
            "body" to post.body,
            "submitter_id" to post.submitter_id,
            "inserted_at" to post.inserted_at,
            "city" to post.city,
            "id" to postsMedia.id,
            "user" to user,
            "post_vote" to listOf<PostVote>(),
            "comment_firestore" to listOf<CommentFirestore>()
        )

        // Saving a post with media

        postMap["media"] = mediaMap
        return Result(postsMedia.id, postsMedia.set(postMap))
    }

    /**
     * savePost - Saves a post, without any media.
     *
     * @post: Post that should be saved
     * @user: user that created the post
     *
     * Return: Result of the operation
     */
    override suspend fun savePost(post: Post, user: User): Result
    {
        val postDoc = db.collection("post").document()
        val postMap = hashMapOf(
            "title" to post.title,
            "body" to post.body,
            "submitter_id" to post.submitter_id,
            "inserted_at" to post.inserted_at,
            "city" to post.city,
            "id" to postDoc.id,
            "user" to user,
            "post_vote" to listOf<PostVote>(),
            "comment_firestore" to listOf<CommentFirestore>(),
        )
        return Result(postDoc.id, postDoc.set(postMap))
    }

    /**
     * getPosts - Reads posts from remote data source
     *
     * @limit: Maximal amount of post that should be read
     *
     * Return: a PostResponse
     */
    override suspend fun getPosts(limit: Long): PostResponse
    {
        val postResponse = PostResponse()
        val loaded = db.collection("post")
            .orderBy("inserted_at", Query.Direction.ASCENDING)
            .limit(limit)
            //.whereNotEqualTo("user.id", FirebaseAuth.getInstance().currentUser.uid)
            .get()
        try
        {
            postResponse.post = loaded.await().documents.mapNotNull { snapshot ->
                snapshot.toObject(PostFirestore::class.java)
            }

        }
        catch (e: Exception)
        {
            postResponse.exception = e
        }
        return postResponse
    }

    /**
     * updatePost - update value of a post.
     *
     * @post: Post that should be updated.
     * @field: The field of the post that will be updated.
     * @value: New value of the field.
     *
     * Return: Google task.
     */
    override suspend fun updatePost(post: PostFirestore, field: String, value: Any): Task<Void>
    {
        val postDoc = db.collection("post").document(post.id)
        val postMap = hashMapOf(
            "title" to post.title,
            "body" to post.body,
            "submitter_id" to post.submitter_id,
            "inserted_at" to post.inserted_at,
            "city" to post.city,
            "id" to postDoc.id,
            "user" to post.user,
            "post_vote" to post.post_vote,
            "comment_firestore" to post.comment_firestore,
            field to value
        )
        return postDoc.set(postMap)
    }

    /**
     * updateComment - updates value of comment.
     *
     * @comment: comment that might be updated
     * @field: field to update
     * @value: New value of the field
     *
     * Return: Google task.
     */
    override suspend fun updateComment(comment: Comment, field: String, value: Any): Task<Void>? {
        val post = getPost(comment.post_id)
        val commentList = post?.comment_firestore
        val commentFirestoreList = mutableListOf<Map<String, Any>>()
        if (commentList != null)
        {
            val commentFirestoreMap = hashMapOf<String, Any>()
            for (element in commentList)
            {
                val commentMap = hashMapOf<String, Any>(
                    "id" to element.comment.id,
                    "body" to element.comment.body,
                    "post_id" to element.comment.post_id,
                    "level" to element.comment.level,
                    "parent_comment" to element.comment.parent_comment,
                    "user_id" to element.comment.user_id,
                    "insert_at" to element.comment.inserted_at,
                )
                if (element.comment.id == comment.id)
                {
                    commentMap[field] = value
                    commentFirestoreMap["comment"] = commentMap
                    commentFirestoreMap["comment_vote"] = element.comment_vote
                    commentFirestoreList.add(commentFirestoreMap)
                    break
                }

                commentFirestoreMap["comment"] = commentMap
                commentFirestoreMap["comment_vote"] = element.comment_vote
                commentFirestoreList.add(commentFirestoreMap)
            }
            return updatePost(post, "comment_firestore", commentFirestoreList)
        }
        return null
    }

    /**
     * getPostSnap: Returns a querySnapshot of post
     * from remote data source
     *
     * @id: id of the post.
     *
     * Return: QuerySnapshot
     */
    override suspend fun getPostSnap(
        id: String
    ): QuerySnapshot
    {
        return db.collection("post")
            .whereEqualTo("id", id)
            .get()
            .await()
    }

    /**
     * getPost - Returns a post from firestore
     *
     * @id: Id of the post to retrieve
     *
     * Return: a PostFirestore element if it exists or null.
     */
    override suspend fun getPost(id: String): PostFirestore?
    {
        var post: PostFirestore? = PostFirestore()
        getPostSnap(id).documents.mapNotNull { documentSnapshot ->
            post = documentSnapshot.toObject(PostFirestore::class.java)
        }
        return post
    }

    /**
     * saveVote - Save vote in remote data source
     *
     * @frameworkData: Post voted
     * @voteType: Type of the vote, either UP or DOWN
     *
     * Return: result of the operation
     */
    override suspend fun saveVote(
        frameworkData: PostFirestore,
        voteType: VoteType
    ): Result?
    {
        if (getPostSnap(frameworkData.id).isEmpty)
            return null
        for (element in frameworkData.post_vote)
        {
            if (element.post_id == frameworkData.id && currentUser?.uid == element.user_id)
            {
                val score = if (voteType == VoteType.DOWN)
                {
                    when (element.score)
                    {
                        1F -> 0F
                        0F -> -1F
                        -1F -> -1F
                        else -> -1F
                    }
                }
                else
                {
                    when (element.score)
                    {
                        1F -> 1F
                        0F -> 1F
                        -1F -> 0F
                        else -> 1F
                    }
                }
                val newPostVoteId = db.collection("postVote").document().id
                val postVote = getPostVoteSnap(currentUser!!.uid, frameworkData.id)
                if (postVote != null)
                {
                    if (postVote.isEmpty)
                    {
                        return Result(
                            frameworkData.id,
                            updatePost(
                                frameworkData,
                                "post_vote",
                                listOf(
                                    mapOf(
                                        "id" to newPostVoteId,
                                        "user_id" to currentUser!!.uid,
                                        "post_id" to frameworkData.id,
                                        "score" to score
                                    )
                                )
                            )
                        )
                    }
                    else
                    {
                        val postVoteResponse: MutableList<Map<String, Any>> = mutableListOf()
                        postVote.documents.mapNotNull { docSnapshot ->
                            val doc = docSnapshot.toObject(PostVote::class.java)
                            val postVoteMap: Map<String, Any>
                            if (doc != null)
                            {
                                postVoteMap =
                                    if (doc.user_id == currentUser!!.uid && doc.post_id == frameworkData.id)
                                    {
                                        mapOf(
                                            "id" to doc.id,
                                            "user_id" to doc.user_id,
                                            "post_id" to doc.post_id,
                                            "score" to score
                                        )
                                    }
                                    else
                                    {
                                        mapOf(
                                            "id" to doc.id,
                                            "user_id" to doc.user_id,
                                            "post_id" to doc.post_id,
                                            "score" to doc.score
                                        )
                                    }
                                postVoteResponse.add(postVoteMap)
                            }
                        }
                        return Result(
                            frameworkData.id,
                            updatePost(frameworkData, "post_vote", postVoteResponse)
                        )
                    }
                }
            }
        }
        return null
    }

    /**
     * getPostVoteSnap - Gets a querySnapshot from remote data source
     *
     * @userId: id of the user to who the post belongs
     * @id: id of the post to retrieve
     *
     * Return - querySnapshot if the post exists or null otherwise
     */
    override suspend fun getPostVoteSnap(
        userId: String,
        id: String
    ): QuerySnapshot?
    {
        return db.collection("postVote").whereEqualTo("user_id", userId)
            .whereEqualTo("post_id", id).get().await()
    }

    /**
     * getPostVote - Gets a vote
     *
     * @userId: id of the user that voted
     * @id: id of the post
     *
     * Return: PostVote element or null
     */
    override suspend fun getPostVote(userId: String, id: String): PostVote? {

        var postVote : PostVote? = null
        db.collection("postVote").whereEqualTo("user_id", userId)
            .whereEqualTo("post_id", id).get().await().documents.mapNotNull {
                postVote = it.toObject(PostVote::class.java)
            }
        return postVote
    }

    /**
     * saveComment - Saves a comment
     *
     * @comment - Comment to save
     *
     * Return: Result of the operation if the comment refers to a post or null
     */
    override suspend fun saveComment(comment: Comment): Result? {

        val postSnapshot = getPostSnap(comment.post_id)
        var loaded: PostFirestore?
        if (postSnapshot.isEmpty)
            return null
        postSnapshot.documents.mapNotNull { documentSnapshot ->
            loaded = documentSnapshot.toObject(PostFirestore::class.java)
            val doc = db.collection("comment").document()
            if (loaded != null)
            {
                val commentList: MutableList<Map<String, Any>> = mutableListOf()
                for (element in loaded!!.comment_firestore) {
                    val commentMap = mapOf(
                        "id" to element.comment.id,
                        "user_id" to element.comment.user_id,
                        "insert_at" to element.comment.inserted_at,
                        "parent_comment" to element.comment.parent_comment,
                        "body" to element.comment.body,
                        "post_id" to element.comment.post_id,
                        "level" to element.comment.level
                    )

                    val commentFirestoreMap = mapOf(
                        "comment" to commentMap,
                        "comment_vote" to element.comment_vote
                    )
                    commentList.add(commentFirestoreMap)
                }
                val commentMap = mapOf(
                    "id" to doc.id,
                    "user_id" to comment.user_id,
                    "insert_at" to comment.inserted_at,
                    "parent_comment" to comment.parent_comment,
                    "body" to comment.body,
                    "post_id" to comment.post_id,
                    "level" to comment.level
                )
                val commentFirestoreMap = mapOf(
                    "comment" to commentMap,
                    "comment_vote" to mutableListOf<CommentVote>()
                )
                commentList.add(
                    commentFirestoreMap
                )
                val task = updatePost(loaded!!, "comment_firestore", commentList)
                return Result(doc.id, task)
            }
        }
        return null
    }

    /**
     * getComments - Retrieves comments from remote data source
     *
     * @postId: Id of the post to which comments belong
     *
     * Return: CommentResponse element
     */
    override suspend fun getComments(postId: String): CommentResponse {
        val post = getPost(postId)
            ?: return CommentResponse(null, java.lang.Exception("no post with the given id"))
        val commentFirestoreList = post.comment_firestore
        Log.d("JustBookService", "loaded post: $post")
        val comments = mutableListOf<Comment>()
        for (element in commentFirestoreList)
        {
            Log.d("JustBookService","loaded post element: $element")
            comments.add(element.comment)
        }
        return CommentResponse(comments, null)
    }

    /**
     * getCommentSnap - Retreives a comment from firestore
     * @id: id of the comment to retrieve
     *
     * Return: Google task
     */
    override suspend fun getCommentSnap(id: String): Task<QuerySnapshot> {
        return db.collection("comment")
            .whereEqualTo("id", id)
            .get()
    }

    /**
     * getComment - Gets comment from firestore
     *
     * @comment: Comment to get, only the id will be used
     *
     * Return: The comment or null
     */
    override suspend fun getComment(comment: Comment): Comment? {
        val post = getPost(comment.post_id)
        var wantedComment: Comment? = null
        val commentList = post?.comment_firestore
        if (commentList != null) {
            for (element in commentList){
                if (element.comment.id == comment.id){
                    wantedComment = element.comment
                }
            }
        }
        return wantedComment
    }

    /**
     * deleteAllPosts - Deletes 1000000 from data source
     */
    override suspend fun deleteAllPosts(){
        val post = getPosts(1000000)
        for (element in post.post!!) {
            db.collection("post").document(element.id).delete()
        }
    }

    companion object
    {
        private var firestoreUsingEmulator = false
        private var storageUsingEmulator = false
    }
}
