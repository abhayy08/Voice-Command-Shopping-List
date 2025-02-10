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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
    modifier: Modifier = Modifier, viewModel: MainViewModel
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

    Scaffold(modifier = modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            VoiceCommandFab(onClick = { viewModel.startListening() })
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
    }, scrollBehavior = scrollBehavior
    )
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


@Composable
fun VoiceCommandFab(
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Rounded.Mic, contentDescription = "Start Listening for command")
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