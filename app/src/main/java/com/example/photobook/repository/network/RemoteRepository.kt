package com.example.photobook.repository.network

import android.net.Uri
import android.util.Log
import com.example.photobook.data.*
import com.example.photobook.utils.Constants.VoteType
import com.example.photobook.utils.Login.currentUser
import com.example.photobook.utils.wrapEspressoIdlingResource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
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
                storage.useEmulator("10.0.2.2", 9199)
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
        wrapEspressoIdlingResource {
            // Getting media from Firestore
            val medias = db.collection("media").document()

            // Creating a map of media to save
            val mediaMap = hashMapOf(
                "title" to media.title,
                "url" to media.url,
                "media_type" to media.media_type,
                "valid" to media.valid
            )
            mediaMap["id"] = if (media.id == "") {
                medias.id
            } else {
                media.id
            }

            // Saving the new media
            return Result(medias.id, medias.set(mediaMap))
        }
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
        wrapEspressoIdlingResource {
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
        wrapEspressoIdlingResource {
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
                "post_vote" to listOf<PostVote>()
            )

            // Saving a post with media

            postMap["media"] = mediaMap
            return Result(postsMedia.id, postsMedia.set(postMap))
        }
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
        wrapEspressoIdlingResource {
            val postDoc = db.collection("post").document()
            val postMap = hashMapOf(
                "title" to post.title,
                "body" to post.body,
                "submitter_id" to post.submitter_id,
                "inserted_at" to post.inserted_at,
                "city" to post.city,
                "id" to postDoc.id,
                "user" to user,
                "post_vote" to listOf<PostVote>()
            )
            return Result(postDoc.id, postDoc.set(postMap))
        }
    }

    /**
     * getPosts - Reads posts from remote data source
     *
     * @limit: Maximal amount of post that should be read
     *
     * Return: a PostResponse
     */
    override suspend fun getPosts(limit: Long, lastSeen: List<LastSeen>?): PostResponse
    {
        wrapEspressoIdlingResource {
            val postResponse = PostResponse()
            val doc = db.collection("post")
                .orderBy("inserted_at", Query.Direction.ASCENDING)
                .limit(limit)
            if (lastSeen != null)
                doc.startAfter(lastSeen.last().id)
            val loaded = doc.get()

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
        wrapEspressoIdlingResource {
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
                field to value
            )
            return postDoc.set(postMap)
        }
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
        wrapEspressoIdlingResource {
            return db.collection("post")
                .whereEqualTo("id", id)
                .get()
                .await()
        }
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
        wrapEspressoIdlingResource {
            var post: PostFirestore? = PostFirestore()
            getPostSnap(id).documents.mapNotNull { documentSnapshot ->
                post = documentSnapshot.toObject(PostFirestore::class.java)
            }
            return post
        }
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
        wrapEspressoIdlingResource {
            if (getPostSnap(frameworkData.id).isEmpty)
                return null
            for (element in frameworkData.post_vote) {
                if (element.post_id == frameworkData.id && currentUser?.uid == element.user_id) {
                    val score = if (voteType == VoteType.DOWN) {
                        when (element.score) {
                            1F -> 0F
                            0F -> -1F
                            -1F -> -1F
                            else -> -1F
                        }
                    } else {
                        when (element.score) {
                            1F -> 1F
                            0F -> 1F
                            -1F -> 0F
                            else -> 1F
                        }
                    }
                    val newPostVoteId = db.collection("postVote").document().id
                    val postVote = getPostVoteSnap(currentUser!!.uid, frameworkData.id)
                    if (postVote != null) {
                        if (postVote.isEmpty) {
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
                        } else {
                            val postVoteResponse: MutableList<Map<String, Any>> = mutableListOf()
                            postVote.documents.mapNotNull { docSnapshot ->
                                val doc = docSnapshot.toObject(PostVote::class.java)
                                val postVoteMap: Map<String, Any>
                                if (doc != null) {
                                    postVoteMap =
                                        if (doc.user_id == currentUser!!.uid && doc.post_id == frameworkData.id) {
                                            mapOf(
                                                "id" to doc.id,
                                                "user_id" to doc.user_id,
                                                "post_id" to doc.post_id,
                                                "score" to score
                                            )
                                        } else {
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
        wrapEspressoIdlingResource {
            return db.collection("postVote").whereEqualTo("user_id", userId)
                .whereEqualTo("post_id", id).get().await()
        }
    }

    /**
     * getPostVote - Gets a vote
     *
     * @userId: id of the user that voted
     * @id: id of the post
     *
     * Return: PostVote element or null
     */
    override suspend fun getPostVote(userId: String, id: String): PostVote?
    {
        wrapEspressoIdlingResource {
            var postVote: PostVote? = null
            db.collection("postVote").whereEqualTo("user_id", userId)
                .whereEqualTo("post_id", id).get().await().documents.mapNotNull {
                    postVote = it.toObject(PostVote::class.java)
                }
            return postVote
        }
    }

    /**
     * deleteAllPosts - Deletes 1000000 from data source
     */
    override suspend fun deleteAllPosts()
    {
        wrapEspressoIdlingResource {
            val post = getPosts(1000000)
            for (element in post.post!!) {
                db.collection("post").document(element.id).delete()
            }
        }
    }

    /**
     * saveImage - Saves image in firebase database
     *
     * @imageName: The name of the image in firebase database
     * @data: A bit array that represents the image
     *
     * Return: An uploadTask.
     */
    override suspend fun saveImage(imageName: String, data: ByteArray): UploadTask {
        wrapEspressoIdlingResource {
            val imageRef = storage.getReference("images/").child(imageName)
            return imageRef.putBytes(data)
        }
    }

    /**
     * saveVideo - Saves video in firebase database
     *
     * @videoName: The name of the video in firebase database
     * @videoUri: An uri that represents the video
     *
     * Return: An uploadTask.
     */
    override suspend fun saveVideo(videoUri: Uri, videoName: String): UploadTask
    {
        wrapEspressoIdlingResource {
            val videoReference = storage.getReference("videos/").child(videoName)
            return videoReference.putFile(videoUri)
        }
    }

    companion object
    {
        private var firestoreUsingEmulator = false
        private var storageUsingEmulator = false
    }
}
