package com.abhay.voicecommandshoppinglist.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.abhay.voicecommandshoppinglist.presentation.main.MainScreen
import com.abhay.voicecommandshoppinglist.presentation.main.MainViewModel
import com.abhay.voicecommandshoppinglist.presentation.ui.theme.VoiceCommandShoppingListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            viewModel.scheduleRestockNotifications()

            val permissionLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = {})

            LaunchedEffect(Unit) {
                permissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }


            VoiceCommandShoppingListTheme {
                MainScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}