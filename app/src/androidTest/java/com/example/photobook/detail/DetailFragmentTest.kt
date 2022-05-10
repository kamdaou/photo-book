package com.example.photobook.detail

import android.app.Application
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.photobook.AndroidMainCoroutineRule
import com.example.photobook.FakeAndroidTestRemoteRepository
import com.example.photobook.R
import com.example.photobook.data.*
import com.example.photobook.main.MainViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class DetailFragmentTest {

    private lateinit var photoBookService: FakeAndroidTestRemoteRepository
    private lateinit var mainViewModel: MainViewModel
    private lateinit var detailViewModel: DetailViewModel

    @get:Rule
    val mainCoroutineRule = AndroidMainCoroutineRule()

    @Before
    fun init() {
        val application = ApplicationProvider.getApplicationContext<Application>()

        photoBookService = FakeAndroidTestRemoteRepository()
        mainViewModel = MainViewModel(application, photoBookService)
        detailViewModel = DetailViewModel(application, photoBookService)

    }


    @Test
    fun postDetails_displayInUI() {

        // GIVEN - A post saved in database
        val post = Post("postId", "userId", Timestamp(10, 20), "post title", "post body")
        val user = User(id = "userId", username = "username")
        val postFirestore = PostFirestore(
            id = post.id,
            submitter_id = "userId",
            inserted_at = Timestamp(10, 20),
            title = "post title",
            body = "post body",
            user = user
        )
        mainViewModel.posts = liveData {
            PostResponse(listOf(postFirestore))
        }
        val comments = listOf(Comment("commentId1", "userId1", Timestamp(10, 19), "this is the first comment"),
            Comment("commentId2", "userId2", Timestamp(10, 30), "this is the second comment"), )
        detailViewModel.comments = liveData {
            CommentResponse(comments)
        }
        val navController = Mockito.mock(NavController::class.java)


        // WHEN - Detail fragment launched
        val bundle = DetailFragmentArgs(postFirestore).toBundle()
        launchFragmentInContainer(bundle, R.style.Theme_Photobook) {
            DetailFragment().also { detailFragment ->
                detailFragment.viewLifecycleOwnerLiveData.observeForever { lifeCycleOwner ->
                    if (lifeCycleOwner != null) {
                        Navigation.setViewNavController(detailFragment.requireView(), navController)
                    }
                }
            }
        }


        // THEN - The post is displayed
        Espresso.onView(withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.withText("post title")))
        Espresso.onView(withId(R.id.body)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.body))
            .check(ViewAssertions.matches(ViewMatchers.withText("post body")))
        Espresso.onView(withId(R.id.username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.username))
            .check(ViewAssertions.matches(ViewMatchers.withText("username")))

        //
        //onView(withId(R.id.comments)).perform(RecyclerViewActions.scrollTo<CommentRecyclerViewAdapter.ViewHolder>(withText("this is the first comment")))
    }


}