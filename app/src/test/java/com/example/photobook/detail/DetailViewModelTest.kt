package com.example.photobook.detail

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.photobook.FakeRemoteRepository
import com.example.photobook.MainCoroutineRule
import com.example.photobook.data.Comment
import com.example.photobook.R
import com.example.photobook.data.CommentResponse
import com.example.photobook.getOrAwaitValue
import com.example.photobook.utils.Constants.Status
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DetailViewModelTest
{
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var comment: Comment
    private lateinit var justBookService: FakeRemoteRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init()
    {
        val application = ApplicationProvider.getApplicationContext<Application>()
        FirebaseApp.initializeApp(application.applicationContext)
        justBookService = FakeRemoteRepository()
        detailViewModel = DetailViewModel(application, justBookService)
        comment = Comment(
            "idComment",
            "userId",
            Timestamp(35, 45),
            post_id = "id1",
            body = "body"
        )
    }

    @Test
    fun loadComments_eventsListener() {
        mainCoroutineRule.pauseDispatcher()
        detailViewModel.loadComments("id1")
        val status = detailViewModel.loadingCommentStatus.getOrAwaitValue()
        mainCoroutineRule.resumeDispatcher()

        MatcherAssert.assertThat(status, Is.`is`(Status.LOADING))
        MatcherAssert.assertThat(
            detailViewModel.loadingCommentStatus.getOrAwaitValue(),
            Is.`is`(Status.DONE)
        )
        MatcherAssert.assertThat(
            detailViewModel.comments?.getOrAwaitValue(),
            Is.`is`(CommentResponse(listOf(comment)))
        )
    }

    @Test
    fun saveComment_eventsListener() {
        mainCoroutineRule.pauseDispatcher()
        detailViewModel.saveComment(comment)
        val status = detailViewModel.saveComment.getOrAwaitValue()
        mainCoroutineRule.resumeDispatcher()

        MatcherAssert.assertThat(status, Is.`is`(Status.LOADING))
        val statusChanged =
            detailViewModel.saveComment.getOrAwaitValue() == Status.DONE || detailViewModel.saveComment.getOrAwaitValue() == Status.ERROR
        MatcherAssert.assertThat(statusChanged, Is.`is`(true))
        val snackBarValueChanged =
            detailViewModel.snackBarContain.getOrAwaitValue() == R.string.comment_saving_error || detailViewModel.snackBarContain.getOrAwaitValue() == R.string.comment_saving_success
        MatcherAssert.assertThat(snackBarValueChanged, Is.`is`(true))
    }
}
