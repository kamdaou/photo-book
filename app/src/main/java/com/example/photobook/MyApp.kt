package com.example.photobook

import android.app.Application
import com.example.photobook.addPost.AddPostViewModel
import com.example.photobook.detail.DetailViewModel
import com.example.photobook.main.MainViewModel
import com.example.photobook.repository.network.IRemoteRepository
import com.example.photobook.repository.network.RemoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp: Application()
{
    override fun onCreate()
    {
        super.onCreate()
        val myModule = module {
            viewModel {
                MainViewModel(
                    get(),
                    get() as IRemoteRepository
                )
            }
            single {
                AddPostViewModel(
                    get() as IRemoteRepository
                )
            }
            single {
                DetailViewModel(get())
            }
            single <IRemoteRepository> {
                RemoteRepository()
            }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}