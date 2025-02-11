package com.abhay.voicecommandshoppinglist.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.voicecommandshoppinglist.domain.model.ShoppingListItem
import com.abhay.voicecommandshoppinglist.presentation.ui.theme.VoiceCommandShoppingListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {

    val state = viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is MainViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }

        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Scaffold(modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
                floatingActionButton = {
                    VoiceCommandFab(
                        startListeningClick = viewModel::startListening,
                        stopListeningClick = viewModel::stopListening,
                        isListening = state.value.isListening
                    )
                },
                topBar = { VoiceCommandTopAppBar(scrollBehavior, viewModel::deleteAllItems) },
                snackbarHost = { SnackbarHost(snackbarHostState) }

            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(state.value.shoppingList) { listItem ->
                        ShoppingListItem(item = listItem)
                    }
                }
            }
            if (state.value.isLoading) {
                LoadingStateOverlay(
                    message = state.value.loadingMessage
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCommandTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior, onClearListClick: () -> Unit
) {
    CenterAlignedTopAppBar(title = {
        Text(text = "Voice Command Shopping List")
    }, actions = {
        IconButton(onClick = onClearListClick) {
            Icon(imageVector = Icons.Rounded.ClearAll, contentDescription = "Clear List")
        }
    }, scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun asdasd() {
    VoiceCommandShoppingListTheme {
        VoiceCommandTopAppBar(scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()), onClearListClick = {})
    }
}

@Composable
fun VoiceCommandFab(
    startListeningClick: () -> Unit,
    stopListeningClick: () -> Unit,
    isListening: Boolean
) {
    FloatingActionButton(
        onClick = { if (isListening) stopListeningClick() else startListeningClick() }
    ) {
        Icon(
            imageVector = if (isListening) Icons.Rounded.MicOff else Icons.Rounded.Mic,
            contentDescription = if (isListening) "Stop Listening" else "Start Listening"
        )
    }
}

@Composable
fun ShoppingListItem(
    modifier: Modifier = Modifier,
    item: ShoppingListItem = ShoppingListItem(name = "Apple", quantity = 10)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Q: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


@Preview(
    showBackground = true
)
@Composable
private fun ListItemPreview() {
    VoiceCommandShoppingListTheme {
        ShoppingListItem()
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    VoiceCommandShoppingListTheme {
//        MainScreen()
    }
}