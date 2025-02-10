package com.abhay.voicecommandshoppinglist.domain.model

import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {

    fun getShoppingList(): Flow<List<ShoppingListItem>>
    suspend fun insertItem(item: ShoppingListItem)
    suspend fun deleteItem(item: ShoppingListItem)
    suspend fun updateItem(item: ShoppingListItem)
    suspend fun getItemByName(name: String): ShoppingListItem?
    suspend fun deleteAllItems()
}
