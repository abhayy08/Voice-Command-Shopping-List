package com.abhay.voicecommandshoppinglist.domain.use_cases

import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.abhay.voicecommandshoppinglist.domain.util.Result

class ShoppingListUseCases @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
) {
    suspend fun addItem(itemName: String, quantity: Int): Result<Unit> {
        return try {
            val existingItem = shoppingListRepository.getItemByName(itemName).data
            if (existingItem == null) {
                shoppingListRepository.insertItem(ShoppingListItem(name = itemName, quantity = quantity))
            } else {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                shoppingListRepository.updateItem(updatedItem)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to add item: ${e.message}")
        }
    }

    suspend fun deleteItem(itemName: String): Result<Unit> {
        return try {
            shoppingListRepository.getItemByName(itemName).data?.let {
                shoppingListRepository.deleteItem(it)
                Result.Success(Unit)
            } ?: Result.Error("Item not found: $itemName")
        } catch (e: Exception) {
            Result.Error("Failed to delete item: ${e.message}")
        }
    }

    fun getShoppingList(): Flow<Result<List<ShoppingListItem>>> {
        return shoppingListRepository.getShoppingList()
    }

    suspend fun deleteAllItems(): Result<Unit> {
        return try {
            shoppingListRepository.deleteAllItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete all items: ${e.message}")
        }
    }
}