package com.example.photobook.repository.database

import androidx.room.*
import com.example.photobook.data.Image
import com.example.photobook.data.Media
import com.example.photobook.data.Post
import com.example.photobook.data.User

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

    @Query("SELECT * FROM image WHERE name = :imageName")
    /**
     * getImage - gets an instance of image class in db
     *
     * @imageName: name (and PK) of the image to be gotten
     */
    suspend fun getImage(imageName: String): Image

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * saveImage - saves image into database
     *
     * @image: The image to save
     */
    suspend fun saveImage(image: Image)

    @Query("SELECT * FROM image")
    /**
     * getImages - gets all images in database
     *
     * Return: List of images
     */
    suspend fun getImages(): List<Image>
}