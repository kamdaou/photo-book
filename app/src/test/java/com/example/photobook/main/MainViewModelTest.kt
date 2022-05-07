package com.example.photobook.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.photobook.FakeRemoteRepository
import com.example.photobook.MainCoroutineRule
import com.example.photobook.data.PostFirestore
import com.example.photobook.data.PostResponse
import com.example.photobook.getOrAwaitValue
import com.example.photobook.utils.Constants
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * MainViewModelTest - Tests logic in mainViewModel
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class MainViewModelTest
{
    private lateinit var mainViewModel: MainViewModel
    private lateinit var postFirestore: PostFirestore

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init()
    {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val fakeRemoteRepository = FakeRemoteRepository()

        mainViewModel = MainViewModel(application, fakeRemoteRepository)
        postFirestore = PostFirestore("title", "body", id = "id1")
    }

    @Test
    fun onPostSelected_navigateToDetailFragmentContainPostId()
    {
        // GIVEN - A view model with a list of posts
        val postResponse =
            PostResponse(listOf(PostFirestore(title = "title", body = "body", id = "id1")))

        mainViewModel.posts = MutableLiveData(postResponse)

        // WHEN - on post selected
        mainViewModel.onPostSelected("id1")

        // THEN - navigateToDetail fragment value is post id
        MatcherAssert.assertThat(
            mainViewModel.navigateToPostDetail.getOrAwaitValue()?.id,
            Is.`is`("id1")
        )
    }

    @Test
    fun loadPost_listenEvents()
    {
        mainCoroutineRule.pauseDispatcher()
        mainViewModel.loadPosts()

        val status = mainViewModel.loadingStatus.getOrAwaitValue()

        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(status, `is`(Constants.Status.LOADING))

        MatcherAssert.assertThat(
            mainViewModel.posts?.getOrAwaitValue(),
            `is`(PostResponse(post = listOf(postFirestore)))
        )

    }


    @Test
    fun saveUpVote_eventsListener()
    {
        mainCoroutineRule.pauseDispatcher()
        mainViewModel.saveUpVote(PostFirestore("title", "body", id = "id1"), "userId")

        val status = mainViewModel.upVoteSaved.getOrAwaitValue()

        mainCoroutineRule.resumeDispatcher()

        MatcherAssert.assertThat(status, Is.`is`(false))
        MatcherAssert.assertThat(mainViewModel.upVoteSaved.getOrAwaitValue(), Is.`is`(true))
    }

    @Test
    fun saveDownVote_eventListener()
    {
        mainCoroutineRule.pauseDispatcher()
        mainViewModel.saveDownVote(postFirestore)

        val status = mainViewModel.downVoteSaved.getOrAwaitValue()

        mainCoroutineRule.resumeDispatcher()

        MatcherAssert.assertThat(status, Is.`is`(false))
        MatcherAssert.assertThat(mainViewModel.downVoteSaved.getOrAwaitValue(), Is.`is`(true))
    }

    @Test
    fun onPostSelected_navigateToDetailFragment_isTrue()
    {
        mainViewModel.loadPosts()

        val posts = mainViewModel.posts?.getOrAwaitValue()

        mainViewModel.onPostSelected("id1")

        MatcherAssert.assertThat(posts, `is`(PostResponse(post = listOf(postFirestore))))
        MatcherAssert.assertThat(
            mainViewModel.navigateToPostDetail.getOrAwaitValue(),
            `is`(postFirestore)
        )
    }
}
