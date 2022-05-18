package com.example.photobook.addPost

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
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
import com.example.photobook.repository.network.IRemoteRepository
import com.example.photobook.utils.Constants
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

/**
 * AddPostViewModel - ViewModel for AddPostFragment
 * that allows basic operation between AddPostFragment and
 * remoteRepository
 *
 * @remoteRepository - the remoteRepository
 */
class AddPostViewModel(
    private val remoteRepository : IRemoteRepository
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
     */
    fun savePost(post: Post, user: User, media: Media? = null)
    {
        if ((post.body == "" || post.title == "") && media == null)
        {
            _snackBarContain.value = R.string.save_post_error_message
        }
        else
        {
            _savingStatus.value = Constants.Status.LOADING
            viewModelScope.launch {
                val loaded: Result = if (media == null )
                    remoteRepository.savePost(post = post, user = user)
                else
                    remoteRepository.savePostMedia(post, media, user)

                post.id = loaded.id
                _savingStatus.postValue(Constants.Status.DONE)
                _snackBarContain.postValue(R.string.post_saving_success)
                Log.d(TAG, "post saved successfully")

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

    /**
     * saveImage - Saves image in firebase storage
     *
     * @imageBitmap: The bitmap of the image
     * @imageName: Name of the image
     */
    fun saveImage(imageBitmap: Bitmap, imageName: String) {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        viewModelScope.launch {
            val uploadTask = remoteRepository.saveImage(imageName, data)
            uploadTask
                .addOnSuccessListener {
                    Log.i(TAG, "successfully saved image")
                    _snackBarContain.value = R.string.successfully_saved_image_message
                }
                .addOnCanceledListener {
                    Log.e(TAG, "saving image image process cancelled")
                    _snackBarContain.value = R.string.unsuccessfully_saved_image_message
                }
                .addOnFailureListener {
                    Log.e(TAG, "error saving image: ${it.message}")
                    _snackBarContain.value = R.string.unsuccessfully_saved_image_message
                }
        }
    }

    /**
     * saveVideo - Saves video in firebase storage
     *
     * @videoUri: The uri of the video
     * @videoName: Name of the video
     */
    fun saveVideo(videoUri: Uri, videoName: String)
    {
        viewModelScope.launch {
            val uploadTask: UploadTask = remoteRepository.saveVideo(videoUri, videoName)

            uploadTask
                .addOnSuccessListener {
                    _snackBarContain.value = R.string.successfully_saved_video_message
                }
                .addOnFailureListener {
                    saveVideo(videoUri, videoName)
                }
                .addOnCanceledListener {
                    _snackBarContain.value = R.string.unsuccessfully_saved_video_message
                }
        }
    }

    fun onSaved()
    {
        _savingStatus.value = null
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
