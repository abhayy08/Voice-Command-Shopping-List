package com.abhay.voicecommandshoppinglist.data

import com.abhay.voicecommandshoppinglist.data.data_source.shopping_list.ShoppingListDao
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingHistoryRepo
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListRepository
import com.abhay.voicecommandshoppinglist.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dao: ShoppingListDao, private val shoppingHistoryRepository: ShoppingHistoryRepo
) : ShoppingListRepository {


    override fun getShoppingList(): Flow<Result<List<ShoppingListItem>>> {
        return flow {
            try {
                dao.getShoppingList().collect { items ->
                    emit(Result.Success(items))
                }
            } catch (e: Exception) {
                emit(Result.Error("Failed to fetch shopping list: ${e.message}"))
            }
        }
    }
    override suspend fun insertItem(item: ShoppingListItem): Result<Unit> {
        return try {
            dao.insertItem(item)
            when (val historyResult = shoppingHistoryRepository.addItem(item.name, item.quantity)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error("Item added but failed to update history: ${historyResult.message}")
            }
        } catch (e: Exception) {
            Result.Error("Failed to insert item: ${e.message}")
        }
    }

    override suspend fun deleteItem(item: ShoppingListItem): Result<Unit> {
        return try {
            dao.deleteItem(item)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete item: ${e.message}")
        }
    }

    override suspend fun updateItem(item: ShoppingListItem): Result<Unit> {
        return try {
            dao.insertItem(item)
            when (val historyResult = shoppingHistoryRepository.addItem(item.name, item.quantity)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error("Item updated but failed to update history: ${historyResult.message}")
            }
        } catch (e: Exception) {
            Result.Error("Failed to update item: ${e.message}")
        }
    }

    override suspend fun getItemByName(name: String): Result<ShoppingListItem?> {
        return try {
            Result.Success(dao.getItemByName(name))
        } catch (e: Exception) {
            Result.Error("Failed to get item by name: ${e.message}")
        }
    }

    override suspend fun deleteAllItems(): Result<Unit> {
        return try {
            dao.deleteAllItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete all items: ${e.message}")
        }
    }

}