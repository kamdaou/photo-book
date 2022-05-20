package com.example.photobook.repository.database

import androidx.room.*
import com.example.photobook.data.*

@Dao
interface PhotoBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * saveUser - Saves user in db
     *
     * @user: the user that should be saved
     */
    suspend fun saveUser(user: User): Long?

    @Update
    /**
     * updateUser - updates user in db
     *
     * @user: the user that should be updated
     */
    suspend fun updateUser(user:User)

    @Query("SELECT * FROM user WHERE id = :id")
    /**
     * getUser - gets user in db
     *
     * @id: Id of the user
     *
     * Return: the user or null
     */
    suspend fun getUser(id: String): User?

    @Query("DELETE FROM user")
    /**
     * deleteUsers - deletes all users in db
     */
    suspend fun deleteUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertPost - saves post in local db
     *
     * @post: the post that should be saved
     */
    suspend fun insertPost(post: Post)

    @Query("SELECT * FROM post")
    /**
     * getPosts - Retrieves posts from local db
     *
     * Return: list of posts or null
     */
    suspend fun getPosts(): List<Post>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertMedia - Saves a media in local db
     *
     * @media: The media that should be saved
     */
    suspend fun insertMedia(media: Media)

    @Query("SELECT * FROM media WHERE id = :id")
    /**
     * getMedias - retrieves media from db
     *
     * @id: id of the media that should be retrieved
     *
     * Return: The media or null
     */
    suspend fun getMedias(id: String): Media?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertLastSeen - Saves the last post retrieved from
     * firestore
     *
     * @lastSeen: A lastSeen instance with element_id
     * equals to id of the last element retrieved
     */
    suspend fun insertLastSeen(lastSeen: LastSeen)

    @Query("SELECT * FROM last_seen ORDER BY id LIMIT(1)")
    /**
     * getLastSeen - Retrieves the last document showed in UI,
     * and the last gotten from firestore
     *
     * Return: List (1 element) of last seen document
     */
    suspend fun getLastSeen(): List<LastSeen>?

    @Update
    /**
     * updateLastSeen - updates the last seen document
     *
     * @lastSeen - New value of the last seen document
     */
    suspend fun updateLastSeen(lastSeen: LastSeen)

    @Query("SELECT * FROM image WHERE name = :imageName")
    suspend fun getImage(imageName: String): Image

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveImage(image: Image)
}