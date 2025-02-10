package com.abhay.voicecommandshoppinglist.data.data_source.shopping_list

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow


@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM ShoppingListItem")
    fun getShoppingList(): Flow<List<ShoppingListItem>>

    @Query("SELECT * FROM ShoppingListItem WHERE name = :name")
    suspend fun getItemByName(name: String): ShoppingListItem?

    @Upsert
    suspend fun insertItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteItem(item: ShoppingListItem)

    @Query("DELETE FROM ShoppingListItem")
    suspend fun deleteAllItems()

//    @Upsert
//    suspend fun modifyItem(item: ShoppingListItem)
}
