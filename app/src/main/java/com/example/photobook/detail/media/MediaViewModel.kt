package com.example.photobook.detail.media

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photobook.data.Media

class MediaViewModel(
    private val app: Application
    ): AndroidViewModel(
    app
)
{
    private var _selectedIndex = MutableLiveData<Int>()
    val selectedIndex: LiveData<Int>
        get() = _selectedIndex

    private var _showPrevious = MutableLiveData<Boolean>()
    val showPrevious: LiveData<Boolean>
        get() = _showPrevious
    private var _showNext = MutableLiveData<Boolean>()
    val showNext: LiveData<Boolean>
        get() = _showNext

    fun showPreviousAndNext(media: Media)
    {
        _showPrevious.value = selectedIndex.value!! > 0
        _showNext.value = selectedIndex.value!! < media.url.size - 1
    }

    fun setSelectedIndex(i: Int)
    {
        if (i in 0..4)
            _selectedIndex.value = i
    }

}
