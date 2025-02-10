package com.abhay.voicecommandshoppinglist.domain.model

interface ShoppingHistoryRepo {
    suspend fun addItem(itemName: String, quantity: Int)
    suspend fun checkForRestock(): String
}