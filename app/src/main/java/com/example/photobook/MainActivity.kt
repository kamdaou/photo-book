package com.example.photobook

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.photobook.work.RefreshDataWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * MainActivity - Class that use our navigation
 * it will display the mainFragment as entry point.
 *
 * Meanwhile, the entry point of the app is
 * AuthenticationActivity
 */
class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        delayedInit()
        setContentView(R.layout.activity_main)
    }

    /**
     * delayedInit - Starts a coroutine scope to start work
     */
    private fun delayedInit() {
        CoroutineScope(Dispatchers.Default).launch {
            setUpRecurringWork()
        }
    }

    /**
     * setUpRecurringWork - Sets up a recurring work,
     * retrieving data from firestore and saving them in local db
     */
    private fun setUpRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWork>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
}
