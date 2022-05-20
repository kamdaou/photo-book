package com.example.photobook.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.photobook.repository.Repository
import com.example.photobook.repository.database.PhotoBookDatabase
import com.example.photobook.repository.network.RemoteRepository

class RefreshDataWork(
    appContext: Context,
    params: WorkerParameters
): CoroutineWorker(appContext, params)
{
    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result
    {
        val db = PhotoBookDatabase.getInstance(applicationContext)
        val repository = Repository(db.photoBookDao, RemoteRepository())
        return try
        {
            repository.refreshImages()
            Result.success()
        }
        catch (e:Exception)
        {
        Result.retry()
        }
    }
}