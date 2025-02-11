package com.abhay.voicecommandshoppinglist.presentation.main

import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem

data class UiState(
    val shoppingList: List<ShoppingListItem> = emptyList(),
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val isListening: Boolean = false
)