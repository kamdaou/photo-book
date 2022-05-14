package com.example.photobook.main

import android.app.Application
import androidx.lifecycle.*
import com.example.photobook.R
import com.example.photobook.data.PostFirestore
import com.example.photobook.data.PostResponse
import com.example.photobook.network.IRemoteRepository
import com.example.photobook.utils.Constants

private const val TAG = "MainviewModel"
/**
 * MainViewModel - ViewModel for interaction between UI
 * and remote data source.
 *
 * @application: Android application
 * @remoteRepository: Interface of our network layer
 */
class MainViewModel(
    application: Application,
    val remoteRepository: IRemoteRepository
    ):AndroidViewModel(application)
{
    var shouldRefresh = MutableLiveData(false)
    var posts: LiveData<PostResponse>? = null

    companion object {
        var lastSeen: PostFirestore? = null
    }

    private val _loadingStatus = MutableLiveData<Constants.Status?>()
    val loadingStatus: MutableLiveData<Constants.Status?>
        get() = _loadingStatus

    private var _upVoteSaved = MutableLiveData<Boolean>()
    val upVoteSaved: LiveData<Boolean>
        get() = _upVoteSaved

    private var _downVoteSaved = MutableLiveData<Boolean>()
    val downVoteSaved: LiveData<Boolean>
        get() = _downVoteSaved

    private val _snackBarContain = MutableLiveData<Int?>()
    val snackBarContain: MutableLiveData<Int?>
        get() = _snackBarContain

    private val _navigateToLoginFragment = MutableLiveData<Boolean>()
    val navigateToLoginFragment: LiveData<Boolean>
        get() = _navigateToLoginFragment

    init {
        loadPosts()
    }

    private var _navigateToPostDetail = MutableLiveData<PostFirestore?>()
    val navigateToPostDetail: LiveData<PostFirestore?>
        get() = _navigateToPostDetail

    /**
     * onPostSelected - Navigates to DetailFragment or shows error message
     *
     * @postId: Id of the selected post
     *
     * Return: Nothing
     */
    fun onPostSelected(postId: String)
    {
        if(posts?.value?.post != null)
        {
            for (i in 0 until posts?.value?.post?.size!!)
            {
                if (posts?.value?.post!![i].id == postId)
                {
                    _navigateToPostDetail.value = posts?.value?.post!![i]
                    break
                }
            }
        }
        else
        {
            snackBarContain.value = R.string.post_not_found_message
        }
    }

    /**
     * onPostDetailNavigated - Sets value of _navigatedToPostDetail to null
     */
    fun onPostDetailNavigated()
    {
        _navigateToPostDetail.value = null
    }

    /**
     * loadPosts - Loads post in our posts live data variable
     */
    fun loadPosts()
    {
        _loadingStatus.postValue(Constants.Status.LOADING)

        posts = liveData {
            emit(remoteRepository.getPosts(limit = 20, lastSeen))
        }
    }

    /**
     * refreshPosts - load list of post without showing it to the user
     */
    fun refreshPosts()
    {
        posts = Transformations.switchMap(shouldRefresh) {
            liveData {
                emit(remoteRepository.getPosts(lastSeen = null))
            }
        }
    }

    /**
     * navigateToLoginFragment - Sets value of
     * _navigateToLoginFragment to true
     */
    fun navigateToLoginFragment()
    {
        _navigateToLoginFragment.value = true
    }

    /**
     * onNavigateToLoginFragment - sets value of _navigateToLoginFragment to false
     */
    fun onNavigateToLoginFragment()
    {
        _navigateToLoginFragment.value = false
    }

    /**
     * onSnackBarShowed - Sets value of snackbarContains to null
     */
    fun onSnackBarShowed()
    {
        _snackBarContain.value = null
    }

    /**
     * postRead - Sets value of loading status to DONE
     * or ERROR according to value of post read
     *
     * @response: Response gotten from firestore
     */
    fun postRead(response: PostResponse) {
        if (response.post != null)
        {
            _loadingStatus.value = Constants.Status.DONE
        }
        if (response.exception != null)
        {
            _loadingStatus.value = Constants.Status.ERROR
        }
    }
}
