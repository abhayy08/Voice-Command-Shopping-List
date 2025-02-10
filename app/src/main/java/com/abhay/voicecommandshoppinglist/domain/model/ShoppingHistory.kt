package com.abhay.voicecommandshoppinglist.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_history")
data class ShoppingHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val timestamp: Long, // Store the time of addition
    val quantity: Int
)
