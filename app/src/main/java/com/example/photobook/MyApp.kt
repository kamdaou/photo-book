package com.example.photobook

import android.app.Application
import com.example.photobook.main.MainViewModel
import com.example.photobook.network.IRemoteRepository
import com.example.photobook.network.RemoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp: Application()
{
    override fun onCreate() {
        super.onCreate()
        val module = module {
            viewModel {
                MainViewModel(get(), get() as IRemoteRepository)
            }
//            viewModel {
//                AddPostViewModel(get() as IRemoteRepository)
//            }
//            viewModel {
//                DetailViewModel(get(), get() as IRemoteRepository)
//            }
            single {
                RemoteRepository() as IRemoteRepository
            }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(module))
        }
    }
}