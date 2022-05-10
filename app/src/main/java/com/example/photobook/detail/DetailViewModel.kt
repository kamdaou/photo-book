package com.example.photobook.detail

import android.app.Application
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.*
import com.example.photobook.R
import com.example.photobook.data.Comment
import com.example.photobook.data.CommentResponse
import com.example.photobook.network.IRemoteRepository
import com.example.photobook.network.RemoteRepository
import com.example.photobook.utils.Constants.Status
import kotlinx.coroutines.launch

class DetailViewModel(
    application: Application,
    private val justBookService: IRemoteRepository
    = RemoteRepository()
): AndroidViewModel(application)
{
    private val _saveComment = MutableLiveData<Status?>()
    val saveComment: MutableLiveData<Status?>
        get() = _saveComment

    private val _snackBarContain = MutableLiveData<Int?>()
    val snackBarContain: MutableLiveData<Int?>
        get() = _snackBarContain

    val TAG = "DetailViewModel"

    private val _loadingCommentStatus = MutableLiveData<Status>()
    val loadingCommentStatus: MutableLiveData<Status>
        get() = _loadingCommentStatus

    var comments: LiveData<CommentResponse>? = null

    /**
     * loadComments - Loads comments of a specific post
     * in the comments variable
     *
     * @id: Id of the post
     */
    fun loadComments(id:String)
    {
        _loadingCommentStatus.value = Status.LOADING
        viewModelScope.launch {
            val loaded = justBookService.getComments(id)
            if (loaded.comment != null)
            {
                _loadingCommentStatus.value = Status.DONE
            }
            else
            {
                _loadingCommentStatus.value = Status.ERROR
            }
            comments = liveData {
                emit(loaded)
            }
        }
    }

    /**
     * saveComment - Saves a comment in the firestore database
     *
     * @comment: comment that shoulb be saved
     */
    fun saveComment(comment: Comment)
    {
        _saveComment.value = Status.LOADING
        viewModelScope.launch {
            val loaded = justBookService.saveComment(comment)

            if (loaded == null)
            {
                _saveComment.value = Status.ERROR
                snackBarContain.value = R.string.comment_saving_error
            }
            else
            {
                loaded.task
                    .addOnSuccessListener {
                    _saveComment.value = Status.DONE
                    snackBarContain.value = R.string.comment_saving_success
                }
                    .addOnFailureListener {
                    _saveComment.value = Status.ERROR
                    snackBarContain.value = R.string.comment_saving_error
                }
            }
        }
    }

    /**
     * onSnackBarShowed - Puts value of _snackBarContain
     * to null
     */
    fun onSnackBarShowed() {
        _snackBarContain.value = null
    }

    val observable = Observer()

    /**
     * Observer - Creates setters and getters for bindable variable
     */
    class Observer: BaseObservable()
    {
        var comment = Comment()

        /**
         * getBody - Gets body of a comment
         *
         * Return: the body
         */
        @Bindable
        fun getBody(): String
        {
            return comment.body
        }

        /**
         * setBody - Sets body of a comment
         *
         * @body: value that should be set
         */
        fun setBody(body: String)
        {
            if (comment.body != body)
            {
                comment.body = body
                notifyPropertyChanged(BR.body)
            }
        }
    }
}
