package com.example.photobook.detail.media

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.photobook.data.Media
import com.example.photobook.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class MediaViewModelTest
{
    private lateinit var _viewModel: MediaViewModel

    @Before
    fun init()
    {
        stopKoin()
        _viewModel = MediaViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun setSelectedIndex_indexSet()
    {
        /* GIVEN - A fresh viewModel */

        /* WHEN - Set selected index */
        _viewModel.setSelectedIndex(3)

        /* THEN - Selected index value changed */
        assertThat(_viewModel.selectedIndex.getOrAwaitValue(), `is`(3))
    }

    @Test
    fun showNextAndPrevious()
    {
        /* GIVEN - A viewModel with selected index = 1 */
        val media = Media(url = listOf("abc", "cde", "efg"))
        _viewModel.setSelectedIndex(1)

        /* WHEN - Calls showPreviousAndNext with the current media */
        _viewModel.showPreviousAndNext(media)

        /* THEN - Both showPrevious and showNext values are true */
        assertThat(_viewModel.showPrevious.getOrAwaitValue(), `is`(true))
        assertThat(_viewModel.showNext.getOrAwaitValue(), `is`(true))
    }
}