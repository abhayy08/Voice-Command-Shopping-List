package com.abhay.voicecommandshoppinglist.di

import android.content.Context
import androidx.work.WorkManager
import com.abhay.voicecommandshoppinglist.data.worker.NotificationWorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNotificationWorkManager(workManager: WorkManager): NotificationWorkManager {
        return NotificationWorkManager(workManager)
    }

}