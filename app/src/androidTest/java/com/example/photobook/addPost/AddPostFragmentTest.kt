package com.example.photobook.addPost

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.photobook.R
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class AddPostFragmentTest
{
    /**
     * savePost_noTextSet_ErrorMessageShowed - Tests
     * if when we try to save a post without adding
     * text and without a media, an error snackBar is showed
     */
    @Test
    fun savePost_noTextSet_ErrorMessageShowed()
    {
        /* GIVEN - A fragment */
        launchFragment()

        /* WHEN - Saves a post */
        onView(withId(R.id.title)).perform(clearText())
        onView(withId(R.id.body)).perform(clearText())
        onView(withId(R.id.save_post_button)).perform(click())

        /* THEN - snackBar is showed properly */
        onView(withText(R.string.save_post_error_message)).check(matches(isDisplayed()))
    }

    /**
     * launchFragment - Launches addPostFragment
     */
    private fun launchFragment()
    {
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInContainer(themeResId = R.style.Theme_JustBook) {
            AddPostFragment().also { addPostFragment ->
                addPostFragment.viewLifecycleOwnerLiveData.observeForever { lifeCycleOwner ->
                    if (lifeCycleOwner != null) {
                        Navigation.setViewNavController(addPostFragment.requireView(), navController)
                    }
                }
            }
        }
    }
}