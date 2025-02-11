package com.abhay.voicecommandshoppinglist.domain.model

import kotlinx.coroutines.flow.Flow
import com.abhay.voicecommandshoppinglist.domain.util.Result

interface ShoppingListRepository {
    fun getShoppingList(): Flow<Result<List<ShoppingListItem>>>
    suspend fun insertItem(item: ShoppingListItem): Result<Unit>
    suspend fun deleteItem(item: ShoppingListItem): Result<Unit>
    suspend fun updateItem(item: ShoppingListItem): Result<Unit>
    suspend fun getItemByName(name: String): Result<ShoppingListItem?>
    suspend fun deleteAllItems(): Result<Unit>
}
