package com.abhay.voicecommandshoppinglist.data.data_source.shopping_list

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem

@Database(
    entities = [ShoppingListItem::class],
    version = 2,
    exportSchema = false
)
abstract class ShoppingListDb: RoomDatabase() {
    abstract val dao: ShoppingListDao

    companion object {
        const val DATABASE_NAME = "shopping_list_db"
    }
}