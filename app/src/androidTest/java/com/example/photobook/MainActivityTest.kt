package com.example.photobook

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.photobook.network.RemoteRepository
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

class MainActivityTest
{
    private lateinit var remoteRepository: RemoteRepository
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * init - initializes repository and *start koin*
     */
    @Before
    fun init()
    {
        runBlocking {
            remoteRepository = RemoteRepository()
            remoteRepository.deleteAllPosts()
        }
    }

    /**
     * savePost - End-to-end tests the app.
     *
     * It open mainfragment, navigates to the save post fragment
     * and add title + body and saves them.
     * Then it will test if the saves data are displayed
     * in the main fragment and if the snackBar is showed too.
     * After that, it will click on the saved data and tests if
     * these data are displayed in the detailed fragment.
     */
    @Test
    fun savePost()
    {
        /* GIVEN - MainActivity with a fresh repository */
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        /* WHEN - Saves a post */
        onView(withId(R.id.add_post_button)).perform(click())
        onView(withId(R.id.title)).perform(replaceText("Test title"))
        onView(withId(R.id.body)).perform(replaceText("Test body"))
        onView(withId(R.id.save_post_button)).perform(click())

        /**
         * THEN - The post is displayed in mainFragment
         * and a snackBar saying that the post
         * has been successfully saved and we can
         * find the post in detailed fragment
         */
        onView(withText(("Test title"))).check(matches(isDisplayed()))
        onView(withText(("Test body"))).check(matches(isDisplayed()))

        onView(withText(R.string.post_saving_success))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.not(
                        Matchers.`is`(getActivity(activityScenario)?.window?.decorView)
                    )
                )
            )
            .check(matches(
                isDisplayed()))

        onView(withText(("Test title"))).perform(click())
        onView(withText("Test title")).check(matches(isDisplayed()))
        activityScenario.close()
    }

    /**
     * getActivity - Gets activity context
     *
     * @activityScenario: A mocked activity that is used for tests
     *
     * Return: MainActivity
      */
    private fun getActivity(activityScenario: ActivityScenario<MainActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }
}
