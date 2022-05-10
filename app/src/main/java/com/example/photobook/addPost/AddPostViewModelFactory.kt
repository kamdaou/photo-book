package com.example.photobook.addPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class AddPostViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPostViewModel::class.java))
            return AddPostViewModel() as T
        throw IllegalArgumentException("unknown view model")
    }
}