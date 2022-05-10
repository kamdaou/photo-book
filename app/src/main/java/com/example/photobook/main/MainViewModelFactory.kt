package com.example.photobook.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photobook.network.RemoteRepository

class MainViewModelFactory(
    private val application: Application
    ):
    ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return MainViewModel( application, RemoteRepository()) as T
        throw IllegalArgumentException("unknown view model class")
    }
}
