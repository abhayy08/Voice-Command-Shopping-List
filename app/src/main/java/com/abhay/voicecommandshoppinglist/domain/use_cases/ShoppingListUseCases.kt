package com.abhay.voicecommandshoppinglist.domain.use_cases

import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShoppingListUseCases @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
) {
    suspend fun addItem(itemName: String, quantity: Int) {
        val existingItem = shoppingListRepository.getItemByName(itemName)
        if (existingItem == null) {
            shoppingListRepository.insertItem(ShoppingListItem(name = itemName, quantity = quantity))
            return
        }
        val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
        shoppingListRepository.updateItem(updatedItem)
    }

    suspend fun deleteItem(itemName: String) {
        shoppingListRepository.getItemByName(itemName)?.let {
            shoppingListRepository.deleteItem(it)
        }
    }

    fun getShoppingList(): Flow<List<ShoppingListItem>> {
        return shoppingListRepository.getShoppingList()
    }

    suspend fun deleteAllItems() {
        shoppingListRepository.deleteAllItems()
    }
}