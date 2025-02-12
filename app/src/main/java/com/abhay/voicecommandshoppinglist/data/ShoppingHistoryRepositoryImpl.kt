package com.abhay.voicecommandshoppinglist.data

import android.util.Log
import com.abhay.voicecommandshoppinglist.data.data_source.shopping_history.ShoppingHistoryDao
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistory
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistoryRepo
import javax.inject.Inject

import com.abhay.voicecommandshoppinglist.domain.util.Result

class ShoppingHistoryRepositoryImpl @Inject constructor(private val dao: ShoppingHistoryDao) :
    ShoppingHistoryRepo {

    override suspend fun addItem(itemName: String, quantity: Int): Result<Unit> {
        return try {
            val newItem = ShoppingHistory(
                itemName = itemName,
                timestamp = System.currentTimeMillis(),
                quantity = quantity
            )
            dao.insertItem(newItem)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to add item to history: ${e.message}")
        }
    }

    override suspend fun checkForRestock(): Result<String> {
        return try {
            val RESTOCK_THRESHOLD = 4 * 24 * 60 * 60 * 1000L
            val thresholdTime = System.currentTimeMillis() - RESTOCK_THRESHOLD
            val item = dao.getRestockSuggestions(thresholdTime)
            Result.Success(item?.let { "Consider restocking: $it" } ?: "")
        } catch (e: Exception) {
            Result.Error("Failed to check for restock: ${e.message}")
        }
    }

}
