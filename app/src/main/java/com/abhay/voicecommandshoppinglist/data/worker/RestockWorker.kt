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
        return try {
            Log.d("WorkManager", "Worker starting execution")

            when (val restockResult = shoppingHistoryRepository.checkForRestock()) {
                is com.abhay.voicecommandshoppinglist.domain.util.Result.Success -> {
                    val restockItem = restockResult.data ?: ""
                    if (restockItem.isNotBlank()) {
                        createNotificationChannel()
                        showNotification(restockItem)
                    }
                    Log.d("WorkManager", "Worker completed successfully")
                    Result.success()
                }
                is com.abhay.voicecommandshoppinglist.domain.util.Result.Error -> {
                    Log.e("WorkManager", "Worker failed: ${restockResult.message}")
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            Log.e("WorkManager", "Worker failed with exception", e)
            Result.failure()
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
            .setSmallIcon(R.mipmap.ic_launcher)
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