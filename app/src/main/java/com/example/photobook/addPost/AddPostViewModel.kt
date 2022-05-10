package com.example.photobook.addPost

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photobook.R
import com.example.photobook.data.Media
import com.example.photobook.data.Post
import com.example.photobook.data.Result
import com.example.photobook.data.User
import com.example.photobook.network.IRemoteRepository
import com.example.photobook.network.RemoteRepository
import com.example.photobook.utils.Constants
import kotlinx.coroutines.launch

/**
 * AddPostViewModel - ViewModel for AddPostFragment
 * that allows basic operation between AddPostFragment and
 * remoteRepository
 *
 * @remoteRepository - the remoteRepository
 */
class AddPostViewModel(
    private val remoteRepository : IRemoteRepository = RemoteRepository()
): ViewModel()
{
    private val _savingStatus = MutableLiveData<Constants.Status?>()
    val savingStatus: MutableLiveData<Constants.Status?>
        get() = _savingStatus

    private val _snackBarContain = MutableLiveData<Int?>()
    val snackBarContain: MutableLiveData<Int?>
        get() = _snackBarContain

    /**
     * savePost - Saves a post
     *
     * @post: The post that should be saved
     * @user: user that is saving the post
     * @media: the media that should be saved with the post, if any
     */
    fun savePost(post: Post, user: User, media: Media?)
    {
        _savingStatus.value = Constants.Status.LOADING
        viewModelScope.launch {
            val loaded: Result = if (media != null)
            {
                remoteRepository.savePostMedia(post, media, user)
            }
            else
            {
                remoteRepository.savePost(post = post, user = user)
            }
            loaded.task
                .addOnSuccessListener {
                    post.id = loaded.id
                    _savingStatus.value = Constants.Status.DONE
                    _snackBarContain.value = R.string.post_saving_success
                }
                .addOnFailureListener {
                    _savingStatus.value = Constants.Status.ERROR
                    _snackBarContain.value = R.string.post_saving_error
                }
        }
    }

    /**
     * saveMedia - Saves a media
     *
     * @media: The media that should be saved
     */
    fun saveMedia(media: Media)
    {
        _savingStatus.value = Constants.Status.LOADING
        viewModelScope.launch {
            val result = remoteRepository.saveMedia(media)
            if (result.task.isSuccessful)
            {
                _savingStatus.value = Constants.Status.DONE
            }
            else
            {
                _savingStatus.value = Constants.Status.ERROR
            }
        }
    }

    /**
     * onSnackBarShowed - puts value of _snackBarContain to null
     */
    fun onSnackBarShowed()
    {
        _snackBarContain.value = null
    }

    val observable = Observer()

    /**
     * Observer - Observable class for some data binding
     */
    class Observer: BaseObservable(){
        var post = Post(submitter_id = "", title = "", body = "", city = "")

        /**
         * getTitle - gets the title of the post
         */
        @Bindable
        fun getTitle(): String
        {
            return post.title
        }

        /**
         * setTitle - sets title of the post
         */
        fun setTitle(title: String)
        {
            if (post.title != title)
            {
                post.title = title
                notifyPropertyChanged(BR.post)
            }
        }

        /**
         * getBody - gets the body of the post
         */
        @Bindable
        fun getBody(): String
        {
            return post.body
        }

        /**
         * setBody - sets Body of the post
         */
        fun setBody(body: String)
        {
            if (post.body != body)
            {
                post.body = body
                notifyPropertyChanged(BR.post)
            }
        }
    }
}
