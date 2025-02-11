package com.abhay.voicecommandshoppinglist.domain.model

import com.abhay.voicecommandshoppinglist.domain.util.Result

interface ShoppingHistoryRepo {
    suspend fun addItem(itemName: String, quantity: Int): Result<Unit>
    suspend fun checkForRestock(): Result<String>
}