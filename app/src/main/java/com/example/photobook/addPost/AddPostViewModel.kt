package com.example.photobook.addPost

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
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

class AddPostViewModel(
    private val justBookService : IRemoteRepository = RemoteRepository()
): ViewModel()
{
    private val _savingStatus = MutableLiveData<Constants.Status?>()
    val savingStatus: MutableLiveData<Constants.Status?>
        get() = _savingStatus

    private val _snackBarContain = MutableLiveData<Int?>()
    val snackBarContain: MutableLiveData<Int?>
        get() = _snackBarContain
    private val _navigateToLoginFragment = MutableLiveData<Boolean>()
    val naviageToLoginFragment: LiveData<Boolean>
        get() = _navigateToLoginFragment


    fun savePost(post: Post, user: User, media: Media?)
    {
        _savingStatus.value = Constants.Status.LOADING
        viewModelScope.launch {
            val loaded: Result = if (media != null)
            {
                justBookService.savePostMedia(post, media, user)
            }
            else
            {
                justBookService.savePost(post = post, user = user)
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

    fun saveMedia(media: Media)
    {
        _savingStatus.value = Constants.Status.LOADING
        viewModelScope.launch {
            val result = justBookService.saveMedia(media)
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

    fun onSnackBarShowed()
    {
        _snackBarContain.value = null
    }

    fun navigateToLoginFragment()
    {
        _navigateToLoginFragment.value = true
    }

    fun onLoginFragmentNavigated()
    {
        _navigateToLoginFragment.value = false
    }

    val observable = Observer()

    class Observer: BaseObservable(){
        var post = Post(submitter_id = "", title = "", body = "", city = "")

        @Bindable
        fun getTitle(): String
        {
            return post.title
        }

        fun setTitle(title: String)
        {
            if (post.title != title)
            {
                post.title = title
                notifyPropertyChanged(BR.post)
            }
        }

        @Bindable
        fun getBody(): String
        {
            return post.body
        }

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
