package com.example.photobook.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DetailViewModel(
    application: Application
): AndroidViewModel(application)
{
    private val _snackBarContain = MutableLiveData<Int?>()
    val snackBarContain: MutableLiveData<Int?>
        get() = _snackBarContain

    /**
     * onSnackBarShowed - Puts value of _snackBarContain
     * to null
     */
    fun onSnackBarShowed() {
        _snackBarContain.value = null
    }
}
