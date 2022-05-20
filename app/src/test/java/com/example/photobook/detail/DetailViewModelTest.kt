package com.example.photobook.detail

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.photobook.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

/**
 * DetailViewModelTest - Tests detailViewModel
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class DetailViewModelTest
{
    @Before
    fun init()
    {
        stopKoin()
    }
    /**
     * onSnackBarShowed_snackBarValueIsNull - Checks if after
     * onSnackBarShowed is called, value of snackBBarContain is null
     */
    @Test
    fun onSnackBarShowed_snackBarValueIsNull()
    {
        /* GIVEN - A fresh detailViewModel */
        val application = ApplicationProvider.getApplicationContext<Application>()
        val detailViewModel = DetailViewModel(application)

        /* WHEN - onSnackBarShowed called */
        detailViewModel.onSnackBarShowed()

        /* THEN - snackBarContains value is null */
        /* TODO find a way to assert null value */
        assertThat(detailViewModel.snackBarContain.getOrAwaitValue(), `is`(nullValue()))
    }
}
