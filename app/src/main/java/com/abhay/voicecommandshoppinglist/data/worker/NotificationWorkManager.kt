package com.abhay.voicecommandshoppinglist.data.worker

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationWorkManager @Inject constructor(
    private val workManager: WorkManager
) {

    fun scheduleNotification() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<RestockWorker>(
            3, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "restock_notification_work",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        workManager.getWorkInfoByIdLiveData(request.id)
            .observeForever { workInfo ->
                Log.d("WorkManager", "Work status: ${workInfo?.state}")
            }
    }

    fun cancelNotificationWork() {
        workManager.cancelUniqueWork("restock_notification_work")
    }

}