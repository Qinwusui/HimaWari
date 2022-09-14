package com.anki.hima.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.anki.hima.ui.screens.chat.ChatScreen
import com.anki.hima.ui.theme.HimaWariTheme
import com.anki.hima.utils.Repository
import com.anki.hima.viewmodel.ChatViewModel

class ChatActivity : ComponentActivity() {
    private val chatViewModel by viewModels<ChatViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HimaWariTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(activity = this@ChatActivity)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatViewModel.stopJob()
    }
}