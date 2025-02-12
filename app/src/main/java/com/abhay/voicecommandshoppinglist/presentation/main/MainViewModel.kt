package com.abhay.voicecommandshoppinglist.presentation.main

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.capitalize
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.voicecommandshoppinglist.data.worker.NotificationWorkManager
import com.abhay.voicecommandshoppinglist.domain.use_cases.ShoppingListUseCases
import com.abhay.voicecommandshoppinglist.domain.use_cases.SpeechRecognitionUseCase
import com.abhay.voicecommandshoppinglist.domain.util.Result
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val speechRecognitionUseCase: SpeechRecognitionUseCase,
    private val shoppListUseCase: ShoppingListUseCases,
    private val notificationWorkManager: NotificationWorkManager
) :
    ViewModel() {

    private val _recognizedText = mutableStateOf("")

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            shoppListUseCase.getShoppingList().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(shoppingList = result.data ?: emptyList()) }
                    }

                    is Result.Error -> {
                        showSnackbar(result.message ?: "Unknown error occurred")
                    }
                }
            }
        }
    }

    private fun changeLoadingState(isLoading: Boolean, loadingMessage: String = "") {
        _uiState.update { it.copy(isLoading = isLoading, loadingMessage = loadingMessage) }
    }


    private fun showSnackbar(message: String) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowSnackbar(message))
        }
    }

    fun scheduleRestockNotifications() {
        notificationWorkManager.scheduleNotification()
    }

    private fun handleIntent(input: String) {
        val intent = input.substringAfter("Intent: ").substringBefore(",").trim()
        val item = input.substringAfter("Extracted Item: ").substringBefore(", Quantity:").trim().capitalizeFirstChar()
        val quantity = input.substringAfter("Quantity: ").trim()

        when (intent) {
            "AddItem" -> addItem(item, quantity.toInt())
            "RemoveItem" -> removeItem(item)
            else -> showSnackbar("Please speak a valid command like \"Add 10 apples\" or  \"Remove 5 bananas\"")
        }
        _recognizedText.value = ""
    }

    fun deleteAllItems() {
        viewModelScope.launch {
            changeLoadingState(true, "Deleting all items...")
            when (val result = shoppListUseCase.deleteAllItems()) {
                is Result.Success -> {
                    showSnackbar("Successfully cleared shopping list")
                }

                is Result.Error -> {
                    showSnackbar(result.message ?: "Failed to clear shopping list")
                }
            }
            changeLoadingState(false)
        }
    }

    private fun addItem(itemName: String, quantity: Int) {
        viewModelScope.launch {
            changeLoadingState(true, "Adding Item...")
            when (val result = shoppListUseCase.addItem(itemName, quantity)) {
                is Result.Success -> {
                    showSnackbar("Successfully added $quantity $itemName")
                }

                is Result.Error -> {
                    showSnackbar(result.message ?: "Failed to add item")
                }
            }
            changeLoadingState(false)
        }
    }

    private fun removeItem(itemName: String) {
        viewModelScope.launch {
            changeLoadingState(true, "Removing Item...")
            when (val result = shoppListUseCase.deleteItem(itemName)) {
                is Result.Success -> {
                    showSnackbar("Successfully removed $itemName")
                }

                is Result.Error -> {
                    showSnackbar(result.message ?: "Failed to remove item")
                }
            }
            changeLoadingState(false)
        }
    }

    fun startListening() {

        val context = getApplication(application)

        if (ContextCompat.checkSelfPermission(
                getApplication(context),
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _uiState.update { it.copy(isListening = true) }
//            Log.d("MainViewModel", uiState.value.isListening.toString())
            speechRecognitionUseCase.startListening { result ->

                result.data?.let { recognizedText ->
                    _recognizedText.value = recognizedText
                    Log.d("MainViewModel", "Recognized text: $recognizedText")
                    handleIntent(recognizedText)
                } ?: showSnackbar(result.message ?: "Unknown error")

                _uiState.update { it.copy(isListening = false) }
//                Log.d("MainViewModel", uiState.value.isListening.toString())
            }
        } else {
            showSnackbar("You need to grant permission to record audio")
        }
    }

    fun stopListening() {
        speechRecognitionUseCase.stopListening()
    }

    fun String.capitalizeFirstChar() = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }


}
