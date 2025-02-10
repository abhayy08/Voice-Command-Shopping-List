package com.abhay.voicecommandshoppinglist.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abhay.voicecommandshoppinglist.R
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistoryRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class RestockWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,

) : CoroutineWorker(context, params) {

    @Inject
    lateinit var shoppingHistoryRepository: ShoppingHistoryRepo

    companion object {
        const val CHANNEL_ID = "restock_notification_channel"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        try {
            Log.d("WorkManager", "Worker starting execution")

            val restockItem = shoppingHistoryRepository.checkForRestock()
            Log.d("WorkManager", restockItem)
            if (restockItem.isNotBlank()) {
                createNotificationChannel()
                showNotification(restockItem)
            }

            Log.d("WorkManager", "Worker completed successfully")
            return Result.success()
        } catch (e: Exception) {
            Log.e("WorkManager", "Worker failed", e)
            return Result.failure()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Restock Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Restock Reminder Channel"
        }

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(restockItem: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Restock Reminder")
            .setContentText("Maybe you need to restock on $restockItem ?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}