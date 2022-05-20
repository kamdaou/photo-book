package com.example.photobook.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import com.example.photobook.FakeAndroidTestRemoteRepository
import com.example.photobook.data.*
import com.example.photobook.repository.database.PhotoBookDao
import com.example.photobook.repository.database.PhotoBookDatabase
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
@LargeTest
class RepositoryTest
{
    private lateinit var database: PhotoBookDatabase
    private lateinit var dao: PhotoBookDao
    private lateinit var repository: Repository
    private lateinit var fakeAndroidTestRemoteRepository: FakeAndroidTestRemoteRepository

    /**
     * init - initializes repositories for tests
     */
    @Before
    fun init()
    {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PhotoBookDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.photoBookDao
        fakeAndroidTestRemoteRepository = FakeAndroidTestRemoteRepository()
        repository = Repository(dao, fakeAndroidTestRemoteRepository)
    }

    /**
     * cleanUpDb - closes db and deletes posts in remoteRepos
     */
    @After
    fun cleanUpDb() = runBlocking {
        database.close()
        fakeAndroidTestRemoteRepository.deleteAllPosts()
    }

    /**
     * savePostMediaAndUser_getPostFirestore - Tests if when we save a post,
     * we can get it by calling getPostFirestore
     */
    @Test
    fun savePostMediaAndUser_getPostFirestore() = runBlocking {
        /* GIVEN - A post, a media and a user saved in local repository */
        val post = Post(
            id = "id",
            submitter_id = "userId",
            title = "title",
            body = "body",
            inserted_at = Timestamp.now(),
            media_id = "mediaId"
        )
        val user = User(
            username = "username",
            id = "userId"
        )
        val media = Media(
            id = "mediaId",
            url = listOf(),
            media_type = "IMAGE",
            title = "title"
        )
        val postFirestore = PostFirestore(
            id = post.id,
            title = post.title,
            body = post.body,
            submitter_id = post.submitter_id,
            inserted_at = post.inserted_at,
            user = user,
            media = media
        )

        repository.saveUser(user)
        repository.savePost(post)
        repository.saveMedia(media)

        /* WHEN - Loading postFirestore */
        val loaded = repository.getPostFirestore()

        /* THEN - Got a postResponse corresponding to what has been saved */
        assertThat(loaded, `is`(PostResponse(listOf(postFirestore), null)))
    }

    /**
     * savePostsInRemoteRepository_refreshPosts_getPosts - Tests
     * if when we save posts in remoteRepository, refresh post will
     * will retrieve them
     */
    @Test
    fun savePostsInRemoteRepository_refreshPosts_getPosts() = runBlocking {
        /* GIVEN - A list of posts saved in remoteRepository */
        val post = Post(
            id = "id",
            submitter_id = "userId",
            title = "title",
            body = "body",
            inserted_at = Timestamp.now(),
            media_id = "mediaId"
        )
        val post1 = Post(
            id = "id1",
            submitter_id = "userId",
            title = "title",
            body = "body",
            inserted_at = Timestamp.now(),
            media_id = "mediaId"
        )
        val post2 = Post(
            id = "id2",
            submitter_id = "userId",
            title = "title",
            body = "body",
            inserted_at = Timestamp.now(),
            media_id = "mediaId"
        )
        val user = User(
            username = "username",
            id = "userId"
        )
        val media = Media(
            id = "mediaId",
            url = listOf(),
            media_type = "IMAGE",
            title = "title"
        )

        post.id = fakeAndroidTestRemoteRepository.savePostMedia(post, media,  user).id
        post1.id = fakeAndroidTestRemoteRepository.savePostMedia(post1, media,  user).id
        post2.id = fakeAndroidTestRemoteRepository.savePostMedia(post2, media,  user).id

        /* WHEN - Refresh posts */
        repository.refreshPosts()

        /* THEN - Got saved post in local repository */
        val loaded = repository.getPostFirestore().post
        val postFirestores = listOf(
            PostFirestore(
                id = post.id,
                title = post.title,
                body = post.body,
                submitter_id = post.submitter_id,
                inserted_at = post.inserted_at,
                user = user,
                media = media
            ),
            PostFirestore(
                id = post1.id,
                title = post1.title,
                body = post1.body,
                submitter_id = post1.submitter_id,
                inserted_at = post1.inserted_at,
                user = user,
                media = media
            ),
            PostFirestore(
                id = post2.id,
                title = post2.title,
                body = post2.body,
                submitter_id = post2.submitter_id,
                inserted_at = post2.inserted_at,
                user = user,
                media = media
            )
        )
        assertThat(loaded?.contains(postFirestores[0]), `is`(true))
        assertThat(loaded?.contains(postFirestores[1]), `is`(true))
        assertThat(loaded?.contains(postFirestores[2]), `is`(true))
    }

    /**
     * refreshImage_imageSavedInDb - Tests that images are saved
     * in database after they have been refreshed
     */
    @Test
    fun refreshImage_imageSavedInDb() = runBlocking {
        /* GIVEN - A remote repository with a list of images */
        val imageName1: String = UUID.randomUUID().toString()
        val data1 = ByteArray(10)
        val imageName2: String = UUID.randomUUID().toString()
        val data2 = ByteArray(10)

        val post = Post(
            id = "id",
            submitter_id = "userId",
            title = "title",
            body = "body",
            inserted_at = Timestamp.now(),
            media_id = "mediaId"
        )
        val post1 = Post(
            id = "id1",
            submitter_id = "userId",
            title = "title",
            body = "body",
            inserted_at = Timestamp.now(),
            media_id = "mediaId"
        )
        val post2 = Post(
            id = "id2",
            submitter_id = "userId",
            title = "title",
            body = "body",
            inserted_at = Timestamp.now(),
            media_id = "mediaId"
        )
        val user = User(
            username = "username",
            id = "userId"
        )
        val media = Media(
            id = "mediaId",
            url = listOf(imageName1, imageName2),
            media_type = "IMAGE",
            title = "title"
        )

        post.id = fakeAndroidTestRemoteRepository.savePostMedia(post, media,  user).id
        post1.id = fakeAndroidTestRemoteRepository.savePostMedia(post1, media,  user).id
        post2.id = fakeAndroidTestRemoteRepository.savePostMedia(post2, media,  user).id

        fakeAndroidTestRemoteRepository.saveImage(imageName1, data1)
        fakeAndroidTestRemoteRepository.saveImage(imageName2, data2)

        /* WHEN - Refreshing images */
        repository.refreshImages()

        /* THEN - Images are being gotten in database */
        val allImages = repository.getImagesFromLocal()
        assertThat(allImages.contains(Image(imageName1, data1)), `is`(true))
        assertThat(allImages.contains(Image(imageName2, data2)), `is`(true))
    }

    /**
     * saveImage_imageGottenInDb - Tests if saved image are gotten
     * in the local db
     */
    @Test
    fun saveImage_imageGottenInDb() = runBlocking {
        /* GIVEN - A fresh repos */
        val image = Image("IMG123", ByteArray(10))

        /* WHEN - Saves an image */
        repository.database.saveImage(image)

        /* THEN - Image gotten in db */
        val loaded = repository.database.getImage("IMG123")
        assertThat(loaded, `is`(image))
    }
}
