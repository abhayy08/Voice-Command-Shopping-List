package com.abhay.voicecommandshoppinglist.data.data_source.shopping_history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistory


@Dao
interface ShoppingHistoryDao {
    @Upsert
    suspend fun insertItem(history: ShoppingHistory)

    @Query("SELECT * FROM shopping_history WHERE itemName = :item ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastAddedItem(item: String): ShoppingHistory?

    @Query("SELECT itemName FROM shopping_history WHERE timestamp < :thresholdTime ORDER BY timestamp ASC LIMIT 1")
    suspend fun getRestockSuggestions(thresholdTime: Long): String?

}
