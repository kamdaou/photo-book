package com.example.photobook

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.GrantPermissionRule
import com.example.photobook.addPost.AddPostViewModel
import com.example.photobook.detail.DetailViewModel
import com.example.photobook.main.MainViewModel
import com.example.photobook.repository.database.PhotoBookDatabase
import com.example.photobook.repository.network.IRemoteRepository
import com.example.photobook.repository.network.RemoteRepository
import com.example.photobook.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class MainActivityTest
{
    private lateinit var remoteRepository: RemoteRepository
    private lateinit var database: PhotoBookDatabase
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    val intentsRules: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionResult: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.CAMERA)

    /**
     * init - initializes repository and *start koin*
     */
    @Before
    fun init()
    {
        database = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                PhotoBookDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        val dao = database.photoBookDao

        remoteRepository = RemoteRepository()
        runBlocking {
            remoteRepository.deleteAllPosts()
        }

        stopKoin()
        val app = ApplicationProvider.getApplicationContext<Application>()

        val myModule = module {
            viewModel {
                MainViewModel(
                    app,
                    remoteRepository as IRemoteRepository,
                    dao
                )
            }
            single {
                AddPostViewModel(
                    remoteRepository as IRemoteRepository,
                    dao
                )
            }
            single {
                DetailViewModel(app)
            }
        }

        startKoin {
            modules(listOf(myModule))
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
        val icon = BitmapFactory.decodeResource(
            InstrumentationRegistry.getTargetContext().getResources(),
            android.R.mipmap.sym_def_app_icon
        )
        val resultData = Intent()

        resultData.putExtra("data", icon)

        val result: Instrumentation.ActivityResult =
            Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        /* WHEN - Saves a post */
        onView(withId(R.id.add_post_button)).perform(click())
        Intents.intending(IntentMatchers.toPackage("com.android.camera2")).respondWith(
            result
        )
        onView(withId(R.id.take_picture)).perform(click())

        Intents.intended(IntentMatchers.toPackage("com.android.camera2"))

        /* Deleting the picture in the ui */
        onView(withId(R.id.delete_taken_image)).perform(click())
        onView(withId(R.id.taken_image)).check(matches(not(isDisplayed())))
        /* Taking the picture again */
        onView(withId(R.id.take_picture)).perform(click())

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
            .check(matches(isDisplayed()))

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

    @Before
    fun registerIdlingResource(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

}
