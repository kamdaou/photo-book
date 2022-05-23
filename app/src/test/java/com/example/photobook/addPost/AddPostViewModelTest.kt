package com.example.photobook.addPost

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.photobook.FakeRemoteRepository
import com.example.photobook.MainCoroutineRule
import com.example.photobook.R
import com.example.photobook.data.Media
import com.example.photobook.data.Post
import com.example.photobook.data.User
import com.example.photobook.getOrAwaitValue
import com.example.photobook.repository.database.PhotoBookDatabase
import com.example.photobook.utils.Constants.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

/**
 * AddPostViewModelTest - Tests AddPostViewModel
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class AddPostViewModelTest
{
    private lateinit var viewModel: AddPostViewModel
    private lateinit var justBookService: FakeRemoteRepository
    private lateinit var post: Post
    private lateinit var user: User
    private lateinit var media: Media
    private lateinit var db: PhotoBookDatabase

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    /**
     * init - initializes remoteRepository and viewModel before starting tests
     */
    @Before
    fun init()
    {
        stopKoin()
        db = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                PhotoBookDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        val dao = db.photoBookDao

        justBookService = FakeRemoteRepository()
        viewModel = AddPostViewModel(justBookService, dao)
        post = Post("id1", title = "title", body = "body", submitter_id = "userId")
        user = User("username", "userId")
        media = Media("mediaId")
    }

    @After
    fun closeDB()
    {
        db.close()
    }

    /**
     * savePost_eventsListener - Tests savePost function
     *
     * When a post is saved, there is some few steps that
     * are done, including changing state and that function tests
     * if everything is well done.
     */
    @Test
    fun savePost_eventsListener()
    {
        /* GIVEN - A fresh viewModel */

        /* WHEN - Saving a post */
        mainCoroutineRule.pauseDispatcher()
        viewModel.savePost(post, user)
        val status = viewModel.savingStatus.getOrAwaitValue()

        mainCoroutineRule.resumeDispatcher()

        /* THEN - Value of savingStatus changed */
        val statusChanged = viewModel.savingStatus.getOrAwaitValue() == Status.DONE || viewModel.savingStatus.getOrAwaitValue() == Status.ERROR
        val snackBarContainChanged = viewModel.snackBarContain.getOrAwaitValue() == R.string.post_saving_success || viewModel.snackBarContain.getOrAwaitValue() == R.string.post_saving_error

        MatcherAssert.assertThat(status, Is.`is`(Status.LOADING))
        MatcherAssert.assertThat(snackBarContainChanged, Is.`is`(true))
        MatcherAssert.assertThat(statusChanged, Is.`is`(true))
    }

    /**
     * savePostMedia_eventsListener - tests savePostMedia function
     *
     * When a post is saved, there is some few steps that
     * are done, including changing state and that function tests
     * if everything is well done.
     */
    @Test
    fun savePostMedia_eventsListener()
    {
        /* GIVEN - A fresh viewModel */

        /* WHEN - Saving a post with a media */
        mainCoroutineRule.pauseDispatcher()
        viewModel.savePost(post, user)
        val status = viewModel.savingStatus.getOrAwaitValue()

        mainCoroutineRule.resumeDispatcher()

        /* THEN - Value of savingStatus changed */
        val statusChanged = viewModel.savingStatus.getOrAwaitValue() == Status.DONE || viewModel.savingStatus.getOrAwaitValue() == Status.ERROR
        val snackBarContainChanged = viewModel.snackBarContain.getOrAwaitValue() == R.string.post_saving_success || viewModel.snackBarContain.getOrAwaitValue() == R.string.post_saving_error

        MatcherAssert.assertThat(status, Is.`is`(Status.LOADING))
        MatcherAssert.assertThat(snackBarContainChanged, Is.`is`(true))
        MatcherAssert.assertThat(statusChanged, Is.`is`(true))
    }

    /**
     * saveMedia_eventsListener - Tests saveMedia function
     *
     * When a media is saved, there is some few steps that
     * are done, including changing state and that function tests
     * if everything is well done.
     */
    @Test
    fun saveMedia_eventsListener()
    {
        /* GIVEN - A fresh viewModel */

        /* WHEN - Saving a media */
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveMedia(media)
        val status = viewModel.savingStatus.getOrAwaitValue()

        mainCoroutineRule.resumeDispatcher()

        /* THEN - Value of savingStatus changed */
        val statusChanged = viewModel.savingStatus.getOrAwaitValue() == Status.DONE || viewModel.savingStatus.getOrAwaitValue() == Status.ERROR

        MatcherAssert.assertThat(status, Is.`is`(Status.LOADING))
        MatcherAssert.assertThat(statusChanged, Is.`is`(true))
    }
}
