package com.example.photobook.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RefreshDataWork(
    appContext: Context,
    params: WorkerParameters
): CoroutineWorker(appContext, params)
{
    override suspend fun doWork(): Result
    {
        TODO("Not yet implemented")
    }
}