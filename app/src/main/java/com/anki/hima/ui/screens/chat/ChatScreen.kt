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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anki.hima.ui.activity.ChatActivity
import com.anki.hima.ui.theme.deep_red
import com.anki.hima.ui.theme.gray
import com.anki.hima.viewmodel.ChatViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.john.waveview.WaveView
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel(), activity: ChatActivity) {
    val msgList by chatViewModel.receiveData.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(gray, false)
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(
            title = {
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
                            focusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedIndicatorColor = Color.Transparent
                        ),

                        trailingIcon = {
                            IconButton(onClick = {
                                chatViewModel.sendMsg(input)
                                input = ""
                                scope.launch {
                                    listState.animateScrollToItem(
                                        if (msgList.size > 2) msgList.size - 1 else msgList.size,
                                        scrollOffset = 20
                                    )
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f)
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
            itemsIndexed(msgList) { i, x ->
                val ori = x.nickName != chatViewModel.getNickName()
                val shape = if (ori) {
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
                val color = if (ori) {
                    gray
                } else {
                    deep_red
                }
                val align = if (ori) {
                    Alignment.Start
                } else {
                    Alignment.End
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .align(align)
                    ) {
                        Text(
                            text = x.nickName,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                    Surface(
                        shape = shape,
                        color = color,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .align(align)
                    ) {
                        Text(
                            text = x.msg,
                            modifier = Modifier.padding(10.dp),
                            color = Color.White
                        )
                    }
                }

            }
        }
    }


}