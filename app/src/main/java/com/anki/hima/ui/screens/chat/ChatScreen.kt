package com.anki.hima.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anki.hima.ui.activity.ChatActivity
import com.anki.hima.ui.theme.deep2_red
import com.anki.hima.ui.theme.deep_gray
import com.anki.hima.ui.theme.deep_red
import com.anki.hima.ui.theme.gray
import com.anki.hima.viewmodel.ChatViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.john.waveview.WaveView

@ExperimentalMaterial3Api
@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel(), activity: ChatActivity) {
    val listState = rememberLazyListState()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(gray, false)
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(title = {
            Text("Chat", color = Color.White)
        }, navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }, actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = gray
        )
        )
    }, bottomBar = {
        var input by remember {
            mutableStateOf("")
        }

        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(end = 10.dp, start = 10.dp)
                .background(gray, RoundedCornerShape(10.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
                    val waveView = WaveView(context, null)
                    waveView.setProgress(40)
                    return@AndroidView waveView
                })
                Row(modifier = Modifier.fillMaxWidth()) {

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        label = {
                            Text(text = "Edit SomeThing..", color = Color.White.copy(alpha = 0.4f))
                        },
                        value = input,
                        onValueChange = { input = it },

                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White.copy(alpha = 0.8f),
                            containerColor = Color.Transparent,
                            cursorColor = Color.White,
                            placeholderColor = Color.Transparent,
                            unfocusedLabelColor = Color.White.copy(0.5f),
                            focusedLabelColor = Color.White.copy(alpha = 0.4f),
                            focusedIndicatorColor = Color.Transparent
                        ),

                        trailingIcon = {
                            IconButton(onClick = {  }) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    )
                }
            }

        }


    }) {
        LazyColumn(
            state = listState, modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            itemsIndexed((0..1).toList()) { i, x ->
                val ori = if (i % 2 == 0) {
                    RoundedCornerShape(
                        10.dp,
                        10.dp,
                        10.dp,
                        0.dp
                    )
                } else {
                    RoundedCornerShape(
                        10.dp,
                        10.dp,
                        0.dp,
                        10.dp
                    )
                }
                val color = listOf(gray, deep_red, deep2_red, deep_gray).random()
                val align = if (i % 2 == 0) {
                    Alignment.Start
                } else {
                    Alignment.End
                }
                val textAlign = if (i % 2 == 0) {
                    TextAlign.Start
                } else {
                    TextAlign.End
                }
                val text = if (i % 2 == 0) {
                    "长安流水下江南"
                } else {
                    "江南依旧在，只是流水不长安"
                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)) {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .align(align)
                    ) {
                        Text(
                            text = if (i % 2 == 0) "琪琪" else "林林",
                            textAlign = textAlign,
                            color = color.copy(alpha = 0.6f)
                        )
                    }
                    Surface(
                        shape = ori,
                        color = color,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .align(align)
                    ) {
                        Text(
                            text = text,
                            textAlign = textAlign,
                            modifier = Modifier.padding(10.dp),
                            color = Color.White
                        )
                    }
                }

            }
        }
    }


}