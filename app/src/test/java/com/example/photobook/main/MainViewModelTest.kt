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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * MainViewModelTest - Tests logic in mainViewModel
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [29])
class MainViewModelTest
{
    private lateinit var mainViewModel: MainViewModel
    private lateinit var postFirestore: PostFirestore

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    /**
     * init - Gets viewModel ready and creates a post for test
     */
    @Before
    fun init()
    {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val fakeRemoteRepository = FakeRemoteRepository()

        mainViewModel = MainViewModel(application, fakeRemoteRepository)
        postFirestore = PostFirestore("title", "body", id = "id1")
    }

    /**
     * onPostSelected_navigateToDetailFragmentContainPostId - Checks
     * if navigateToPostDetail contains the good value
     */
    @Test
    fun onPostSelected_navigateToDetailFragmentContainPostId()
    {
        /* GIVEN - A view model with a list of posts */
        val postResponse =
            PostResponse(listOf(PostFirestore(title = "title", body = "body", id = "id1")))

        mainViewModel.posts = MutableLiveData(postResponse)

        /* WHEN - on post selected */
        mainViewModel.onPostSelected("id1")

        /* THEN - navigateToDetail fragment value is post id */
        MatcherAssert.assertThat(
            mainViewModel.navigateToPostDetail.getOrAwaitValue()?.id,
            Is.`is`("id1")
        )
    }

    /**
     * loadPost_listenEvents - Checks if events are well handled
     * by viewModel when loadPosts fun is called.
     */
    @Test
    fun loadPost_listenEvents()
    {
        /* GIVEN - A viewModel with a list of posts */
        mainCoroutineRule.pauseDispatcher()

        /* WHEN - loadPosts is called */
        mainViewModel.loadPosts()

        val status = mainViewModel.loadingStatus.getOrAwaitValue()

        mainCoroutineRule.resumeDispatcher()

        /* THEN - status is loading and at the end, value of posts contains the right value */
        MatcherAssert.assertThat(status, `is`(Constants.Status.LOADING))
        MatcherAssert.assertThat(
            mainViewModel.posts?.getOrAwaitValue(),
            `is`(PostResponse(post = listOf(postFirestore)))
        )

    }

    /**
     * onPostSelected_navigateToDetailFragment_isTrue - Checks
     * if when onPostSelected is called, value of
     * navigateToDetailFragment is true
     */
    @Test
    fun onPostSelected_navigateToDetailFragment_isTrue()
    {
        /* GIVEN - A viewModel with a list of posts */
        mainViewModel.loadPosts()

        val posts = mainViewModel.posts?.getOrAwaitValue()

        /* WHEN - OnPostSelected called */
        mainViewModel.onPostSelected("id1")

        /* THEN - navigateToPostDetail contains the right value */
        MatcherAssert.assertThat(posts, `is`(PostResponse(post = listOf(postFirestore))))
        MatcherAssert.assertThat(
            mainViewModel.navigateToPostDetail.getOrAwaitValue(),
            `is`(postFirestore)
        )
    }
}
