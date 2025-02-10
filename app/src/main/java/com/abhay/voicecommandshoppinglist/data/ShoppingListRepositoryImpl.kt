package com.abhay.voicecommandshoppinglist.data

import com.abhay.voicecommandshoppinglist.data.data_source.shopping_list.ShoppingListDao
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistoryRepo
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListRepository
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dao: ShoppingListDao,
    private val shoppingHistoryRepository: ShoppingHistoryRepo
): ShoppingListRepository {
    override fun getShoppingList(): Flow<List<ShoppingListItem>> {
        return dao.getShoppingList()
    }

    override suspend fun insertItem(item: ShoppingListItem) {
        dao.insertItem(item)
        shoppingHistoryRepository.addItem(item.name, item.quantity)
    }

    override suspend fun deleteItem(item: ShoppingListItem) {
        dao.deleteItem(item)
    }

    override suspend fun updateItem(item: ShoppingListItem) {
        dao.insertItem(item)
        shoppingHistoryRepository.addItem(item.name, item.quantity)
    }

    override suspend fun getItemByName(name: String): ShoppingListItem? {
        return dao.getItemByName(name)
    }

    override suspend fun deleteAllItems() {
        dao.deleteAllItems()
    }

}