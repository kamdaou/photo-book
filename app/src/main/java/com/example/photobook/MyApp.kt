package com.example.photobook

import android.app.Application
import com.example.photobook.addPost.AddPostViewModel
import com.example.photobook.detail.DetailViewModel
import com.example.photobook.detail.media.MediaViewModel
import com.example.photobook.main.MainViewModel
import com.example.photobook.repository.database.PhotoBookDao
import com.example.photobook.repository.database.PhotoBookDatabase
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
                    get() as IRemoteRepository,
                    get()
                )
            }
            single<PhotoBookDao> {
                PhotoBookDatabase.getInstance(get()).photoBookDao
            }
            single {
                AddPostViewModel(
                    get() as IRemoteRepository,
                    get()
                )
            }
            single {
                DetailViewModel(get())
            }
            single <IRemoteRepository> {
                RemoteRepository()
            }
            single {
                MediaViewModel(
                    get()
                )
            }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}