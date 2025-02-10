package com.abhay.voicecommandshoppinglist.data

import android.util.Log
import com.abhay.voicecommandshoppinglist.data.data_source.shopping_history.ShoppingHistoryDao
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistory
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistoryRepo
import javax.inject.Inject

class ShoppingHistoryRepositoryImpl @Inject constructor(private val dao: ShoppingHistoryDao) :
    ShoppingHistoryRepo {

    override suspend fun addItem(itemName: String, quantity: Int) {
        val newItem = ShoppingHistory(
            itemName = itemName, timestamp = System.currentTimeMillis(), quantity = quantity
        )
        dao.insertItem(newItem)
    }

    override suspend fun checkForRestock(): String {
        val RESTOCK_THRESHOLD = 7 * 24 * 60 * 60 * 1000L

        val thresholdTime = System.currentTimeMillis() - RESTOCK_THRESHOLD
        val item = dao.getRestockSuggestions(thresholdTime)
        Log.d("ShoppingHistoryRepo", "Restock suggestions: $item")
        return item?.let { "Consider restocking: $it" } ?: ""
    }
}
