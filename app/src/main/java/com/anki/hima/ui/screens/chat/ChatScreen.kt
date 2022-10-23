package com.anki.hima.ui.screens.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anki.hima.ui.theme.deep_red
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.toastShort
import com.anki.hima.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.john.waveview.WaveView
import kotlinx.coroutines.launch

@SuppressLint(
    "UnrememberedMutableState", "CoroutineCreationDuringComposition",
    "MutableCollectionMutableState"
)
@ExperimentalMaterial3Api
@Composable
fun ChatScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController,
) {
    mainViewModel.loadMsgList()
    val context = LocalContext.current
    val waveView = WaveView(context, null)
    waveView.setProgress(40)
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    val chatRoomMsgList by mainViewModel.chatRoomMsgList.collectAsState()
    val msg by mainViewModel.msgData.collectAsState()
    val _msg by rememberUpdatedState(newValue = msg)
    val chatRoomMsgData by rememberUpdatedState(newValue = chatRoomMsgList)

    val login = mainViewModel.login
    scope.launch {
        listState.animateScrollToItem(chatRoomMsgData.size)
    }
    SideEffect {
        systemUiController.setSystemBarsColor(gray, false)
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(mainViewModel.chatRoomId, color = Color.White)
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
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
                AndroidView(modifier = Modifier.fillMaxWidth(), factory = { _ ->
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

                                if (login) {
                                    mainViewModel.sendMsg(msg = input)
                                    input = ""
                                } else {
                                    "你还没有登录呢...".toastShort()
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
            itemsIndexed(chatRoomMsgData) { _, x ->
                val ori = x.nickName != mainViewModel.getNickName()
                val shape = if (ori) {
                    RoundedCornerShape(
                        10.dp,
                        10.dp,
                        10.dp,
                        3.dp
                    )
                } else {
                    RoundedCornerShape(
                        10.dp,
                        10.dp,
                        3.dp,
                        10.dp
                    )
                }
                val color = if (ori) {
                    gray
                } else {
                    deep_red
                }
                val align = if (ori) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                ) {

                    //消息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = align,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (ori) {
                            Card(shape = RoundedCornerShape(50.dp)) {
                                AsyncImage(
                                    model = "https://q2.qlogo.cn/headimg_dl?dst_uin=${
                                        x.qq
                                    }&spec=100",
                                    contentDescription = null
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            //昵称展示
                            Surface {
                                Text(
                                    text = x.nickName,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        color,
                                        shape
                                    )
                                    .align(if (ori) Alignment.Start else Alignment.End),
                            ) {
                                Text(
                                    text = x.msg,
                                    modifier = Modifier.padding(10.dp),
                                    color = Color.White
                                )
                            }

                        }
                        if (!ori) {
                            Card(shape = RoundedCornerShape(50.dp)) {
                                AsyncImage(
                                    model = "https://q2.qlogo.cn/headimg_dl?dst_uin=${
                                        mainViewModel.getQQ()
                                    }&spec=100",
                                    contentDescription = null
                                )
                            }
                        }
                    }
//                        Surface(
//                            shape = shape,
//                            color = color,
//                            modifier = Modifier
//                                .padding(horizontal = 10.dp)
//                                .align(align)
//                        ) {
//
//                        }

                }


            }
        }
    }


}