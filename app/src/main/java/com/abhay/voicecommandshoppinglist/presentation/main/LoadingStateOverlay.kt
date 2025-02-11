package com.abhay.voicecommandshoppinglist.presentation.main

import android.os.Message
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.voicecommandshoppinglist.presentation.ui.theme.VoiceCommandShoppingListTheme

@Composable
fun LoadingStateOverlay(
    modifier: Modifier = Modifier,
    message: String
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            strokeCap = StrokeCap.Round
        )
        Text(text = message, style = MaterialTheme.typography.titleLarge)
    }
}

@Preview
@Composable
private fun LoadingStateOverlayPreview() {
    VoiceCommandShoppingListTheme {
        LoadingStateOverlay(message = "Adding Items...")
    }
}