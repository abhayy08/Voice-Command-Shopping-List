package com.abhay.voicecommandshoppinglist.data.data_source.shopping_history

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistory


@Database(entities = [ShoppingHistory::class], version = 1, exportSchema = false)
abstract class ShoppingHistoryDb: RoomDatabase() {
    abstract val dao: ShoppingHistoryDao
    companion object {
        const val DATABASE_NAME = "shopping_history_db"
    }
}