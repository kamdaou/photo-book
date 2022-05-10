package com.example.photobook.main

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.photobook.AndroidMainCoroutineRule
import com.example.photobook.FakeAndroidTestRemoteRepository
import com.example.photobook.R
import com.example.photobook.ServiceLocator
import com.example.photobook.adapters.PostRecyclerViewAdapter
import com.example.photobook.data.Post
import com.example.photobook.data.PostFirestore
import com.example.photobook.data.User
import com.example.photobook.network.IRemoteRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class MainFragmentTest {
    private lateinit var post: Post
    private lateinit var postFirestore: PostFirestore
    private lateinit var remoteRepository: IRemoteRepository
    private lateinit var user: User
    private lateinit var application: Application

    @get:Rule
    val mainCoroutineRule = AndroidMainCoroutineRule()

    /**
     * init - initializes view model and all dependencies
     */
    @Before
    fun init()
    {
        application = ApplicationProvider.getApplicationContext()

        remoteRepository = FakeAndroidTestRemoteRepository()
        ServiceLocator.remoteRepository = remoteRepository
        post = Post(
            id = "postId",
            submitter_id = "userId",
            inserted_at = Timestamp(600000, 344000),
            title = "title",
            body = "body",
        )
        postFirestore = PostFirestore(
            id = "postId",
            submitter_id = "userId",
            inserted_at = Timestamp(600000, 344000),
            title = "title",
            body = "body",
        )
        user = User(username = "username", id = "userId")
    }

    /**
     * postDisplayedInUI - tests if saved post is displayed in the main fragment
     */
    @Test
    fun postDisplayedInUI()
    {
        runBlockingTest {
            savePost(user)
            val navController = launchFragment()

            Espresso.onView(withId(R.id.post_list))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(withId(R.id.post_list))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<PostRecyclerViewAdapter.ViewHolder>(
                        0,
                        ViewActions.click()
                    )
                )
            Mockito.verify(navController).navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(postFirestore))
        }
    }

    /**
     * savePost - Saves a post
     *
     * @user: The user submitting the post
     */
    private suspend fun savePost(user: User)
    {
        val result = remoteRepository.savePost(post, user)
        postFirestore.user = user
        postFirestore.id = result.id
    }

    /**
     * launchFragment - Create a fragment scenario and launch it
     * Returns a mock navController
     */
    private fun launchFragment(): NavController
    {
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInContainer(Bundle(), R.style.Theme_JustBook) {
            MainFragment().also { mainFragment ->
                mainFragment.viewLifecycleOwnerLiveData.observeForever { lifeCycleOwner ->
                    if (lifeCycleOwner != null) {
                        Navigation.setViewNavController(mainFragment.requireView(), navController)
                    }
                }
            }
        }
        return (navController)
    }

    /**
     * clickOnAddPostButton_navigateToAddPostFragment - Checks if clicking on button
     * to add post causes a navigation to addPostFragment
     */
    @Test
    fun clickOnAddPostButton_navigateToAddPostFragment(){
        val navController = launchFragment()

        Espresso.onView(withId(R.id.add_post_button)).perform(ViewActions.click())
        Mockito.verify(navController).navigate(R.id.action_mainFragment_to_addPostFragment)
    }
}
