package com.example.photobook.repository

import android.util.Log
import com.example.photobook.data.*
import com.example.photobook.repository.database.PhotoBookDao
import com.example.photobook.repository.network.IRemoteRepository
import com.example.photobook.utils.Converter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val TAG = "Repository"
class Repository(
    private val database: PhotoBookDao,
    private val remoteRepository: IRemoteRepository
    )
{
    /**
     * refreshPosts - Reads posts from remote source then
     * saves them in the local db for data persistence.
     */
    suspend fun refreshPosts()
    {
        try {
            withContext(Dispatchers.IO){
                val lastPost = database.getLastSeen()
                val postResponse = remoteRepository.getPosts(lastSeen = lastPost)

                if (postResponse.post != null)
                {
                    val lastSeen = LastSeen(
                        element_id = postResponse.post!!.last().id
                    )
                    saveLastSeen(lastSeen)
                }
                if (postResponse.post != null)
                {
                    for (element in postResponse.post!!)
                    {
                        val post = Post(
                            id = element.id,
                            submitter_id = element.submitter_id,
                            inserted_at = element.inserted_at,
                            city = element.city,
                            body = element.body,
                            title = element.title,
                            media_id = if (element.media == null) "" else element.media!!.id
                        )

                        savePost(post)
                        if (element.media != null)
                            saveMedia(element.media!!)
                        if (element.user != null)
                            saveUser(user = element.user!!)
                    }
                }
            }
        }
        catch (e:Exception)
        {
            Log.e(TAG, "error refreshing post: ${e.message}")
        }
    }

    /**
     * savePost - saves post in local db
     *
     * @post: the post that should be saved
     */
    suspend fun savePost(post: Post)
    {
        withContext(Dispatchers.IO) {
            database.insertPost(post)
        }
    }

    /**
     * getPosts - Retrieves posts from local db
     *
     * Return: list of posts
     */
    suspend fun getPosts(): List<Post>? = withContext(Dispatchers.IO) {
        return@withContext database.getPosts()
    }

    /**
     * saveUser - saves user in local db
     *
     * @user: the user that should be saved
     */
    suspend fun saveUser(user: User)
    {
        withContext(Dispatchers.IO) {
            database.saveUser(user)
        }
    }

    /**
     * saveMedia - Saves a media in local db
     *
     * @media: The media that should be saved
     */
    suspend fun saveMedia(media: Media)
    {
        withContext(Dispatchers.IO) {
            database.insertMedia(media)
        }
    }

    /**
     * savesLastSeen - Saves the last post retrieved from
     * firestore
     *
     * @lastSeen: A lastSeen instance with element_id
     * equals to id of the last element retrieved
     */
    suspend fun saveLastSeen(lastSeen: LastSeen)
    {
        withContext(Dispatchers.IO) {
            database.insertLastSeen(lastSeen)
        }
    }

    /**
     * getPostFirestore - retrieves post from database and convert them
     * into postFirestore instance
     *
     * Return: A postResponse
     */
    suspend fun getPostFirestore(): PostResponse = withContext(Dispatchers.IO){
        val post: List<Post>?
        var media: Media?
        var user: User?
        val postFirestore: MutableList<PostFirestore> = mutableListOf()
        var exception: Exception? = null
        try
        {
            post = database.getPosts()
            if (post != null)
                for (element in post)
                {
                    media = database.getMedias(element.media_id)
                    user = database.getUser(element.submitter_id)
                    postFirestore.add(Converter().postToPostFirestore(element, media, user))
                }
        }
        catch (e: Exception)
        {
            exception = e
        }

        return@withContext PostResponse(postFirestore, exception)
    }
}