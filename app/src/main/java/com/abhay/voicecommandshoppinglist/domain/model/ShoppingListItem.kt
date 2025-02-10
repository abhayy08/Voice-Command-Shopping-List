package com.abhay.voicecommandshoppinglist.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int = 1,
    val checked: Boolean = false,
)