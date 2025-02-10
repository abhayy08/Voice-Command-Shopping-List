package com.abhay.voicecommandshoppinglist.di

import android.content.Context
import androidx.room.Room
import com.abhay.voicecommandshoppinglist.data.NLPProcessor
import com.abhay.voicecommandshoppinglist.data.ShoppingHistoryRepositoryImpl
import com.abhay.voicecommandshoppinglist.data.ShoppingListRepositoryImpl
import com.abhay.voicecommandshoppinglist.data.SpeechRecognitionServiceImpl
import com.abhay.voicecommandshoppinglist.data.data_source.shopping_list.ShoppingListDao
import com.abhay.voicecommandshoppinglist.data.data_source.shopping_list.ShoppingListDb
import com.abhay.voicecommandshoppinglist.data.data_source.shopping_history.ShoppingHistoryDao
import com.abhay.voicecommandshoppinglist.data.data_source.shopping_history.ShoppingHistoryDb
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistoryRepo
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListRepository
import com.abhay.voicecommandshoppinglist.domain.model.SpeechRecognitionService
import com.abhay.voicecommandshoppinglist.domain.use_cases.ShoppingListUseCases
import com.abhay.voicecommandshoppinglist.domain.use_cases.SpeechRecognitionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNLPProcessor(): NLPProcessor {
        return NLPProcessor()
    }

    @Provides
    @Singleton
    fun provideShoppingListDatabase(@ApplicationContext app: Context): ShoppingListDb {
        return Room.databaseBuilder(app, ShoppingListDb::class.java, ShoppingListDb.DATABASE_NAME).build()
    }

    @Provides
    fun provideShoppingListDao(db: ShoppingListDb): ShoppingListDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun provideShoppingListRepository(dao: ShoppingListDao, shoppingHistoryRepositoryImpl: ShoppingHistoryRepositoryImpl): ShoppingListRepository {
        return ShoppingListRepositoryImpl(dao, shoppingHistoryRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideShoppingListUseCase(repository: ShoppingListRepository): ShoppingListUseCases {
        return ShoppingListUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideShoppingHistoryDatabase(@ApplicationContext app: Context): ShoppingHistoryDb {
        return Room.databaseBuilder(app, ShoppingHistoryDb::class.java, ShoppingHistoryDb.DATABASE_NAME).build()
    }


    @Provides
    fun provideShoppingHistoryDao(db: ShoppingHistoryDb): ShoppingHistoryDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun provideShoppingHistoryRepository(dao: ShoppingHistoryDao): ShoppingHistoryRepo {
        return ShoppingHistoryRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideSpeechRecognitionRepository(@ApplicationContext context: Context, nlpProcessor: NLPProcessor): SpeechRecognitionService {
        return SpeechRecognitionServiceImpl(context, nlpProcessor)
    }

    @Provides
    @Singleton
    fun provideSpeechRecognitionUseCase(repository: SpeechRecognitionService): SpeechRecognitionUseCase {
        return SpeechRecognitionUseCase(repository)
    }

}
