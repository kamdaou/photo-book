package com.example.photobook

import androidx.annotation.VisibleForTesting
import com.example.photobook.repository.network.IRemoteRepository
import com.example.photobook.repository.network.RemoteRepository
import kotlinx.coroutines.runBlocking

/**
 * ServiceLocator - create photoBook service for testing asynchronously
 */
object ServiceLocator
{
    private val lock = Any()

    @Volatile
    var remoteRepository: IRemoteRepository? = null
        @VisibleForTesting set

    /**
     * provideRemoteRepository - create a fake photoBook Service
     *
     * Return: a fresh create photoBook Service
     *
     */
    fun provideRemoteRepository(): IRemoteRepository
    {
        synchronized(this){
            return remoteRepository?: RemoteRepository()
        }
    }

    /**
     * resetService - Delete all data from database before closing
     *
     * Return: nothing
     */
    @VisibleForTesting
    fun resetService()
    {
        synchronized(lock)
        {
            runBlocking {
                RemoteRepository().deleteAllPosts()
            }
        }
        remoteRepository = null
    }

}
