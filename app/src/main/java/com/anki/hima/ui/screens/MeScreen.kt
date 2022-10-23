package com.anki.hima.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anki.hima.R
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.toastShort
import com.anki.hima.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AboutScreen(mainViewModel: MainViewModel) {
    val listState = rememberLazyListState()
    val sign = mainViewModel.sign
    val login = mainViewModel.login
    var uName by remember {
        mutableStateOf("")
    }
    var qq by remember {
        mutableStateOf("")
    }
    var pwd by remember {
        mutableStateOf("")
    }
    var needSignIn by remember {
        mutableStateOf(false)
    }
    DisposableEffect(key1 = sign) {
        if (sign) {
            needSignIn = false
        }
        onDispose {

        }
    }
    DisposableEffect(key1 = login) {
        if (login) {
            needSignIn = false
        }
        onDispose {}
    }
    AnimatedVisibility(visible = needSignIn) {
        AlertDialog(
            onDismissRequest = { needSignIn = false },
            containerColor = gray,
            title = {
                Text(text = "注册", color = Color.White)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    TextField(
                        value = uName,
                        onValueChange = {
                            uName = it
                        },
                        label = {
                            Text(text = "用户名", color = Color.White.copy(alpha = 0.4f))
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White.copy(alpha = 0.8f),
                            containerColor = Color.Transparent,
                            cursorColor = Color.White,
                            placeholderColor = Color.Transparent,
                            unfocusedLabelColor = Color.White.copy(0.5f),
                            focusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedIndicatorColor = Color.White
                        ),
                    )
                    TextField(
                        value = qq,
                        onValueChange = {
                            qq = it

                        },
                        label = {
                            Text(text = "QQ号", color = Color.White.copy(alpha = 0.4f))
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White.copy(alpha = 0.8f),
                            containerColor = Color.Transparent,
                            cursorColor = Color.White,
                            placeholderColor = Color.Transparent,
                            unfocusedLabelColor = Color.White.copy(0.5f),
                            focusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedIndicatorColor = Color.White
                        ),
                    )
                    TextField(
                        value = pwd,
                        onValueChange = {
                            pwd = it

                        },
                        label = {
                            Text(text = "密码", color = Color.White.copy(alpha = 0.4f))
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White.copy(alpha = 0.8f),
                            containerColor = Color.Transparent,
                            cursorColor = Color.White,
                            placeholderColor = Color.Transparent,
                            unfocusedLabelColor = Color.White.copy(0.5f),
                            focusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedIndicatorColor = Color.White
                        ),
                    )
                }

            },
            confirmButton = {
                Row {
                    TextButton(onClick = { needSignIn = false }) {
                        Text(text = "取消", color = Color.White)
                    }
                    TextButton(onClick = {
                        mainViewModel.signIn(uName, qq, pwd)
                    }) {
                        Text(text = "注册", color = Color.White)
                    }
                    TextButton(onClick = {
                        mainViewModel.login(uName, qq, pwd)
                    }) {
                        Text(text = "登录", color = Color.White)
                    }
                }

            })
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        item {
            ListItem {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier
                            .size(100.dp), shape = RoundedCornerShape(50.dp)
                    ) {
                        AsyncImage(
                            model = if (login) "https://q2.qlogo.cn/headimg_dl?dst_uin=${
                                mainViewModel.getQQ() 
                            }&spec=100" else R.drawable.ic_launcher_foreground,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .combinedClickable(
                                    onClick = {
                                        if (login) {
                                            "你已经登录过了".toastShort()
                                        } else {
                                            needSignIn = true
                                        }
                                    },
                                    onLongClick = {
                                        mainViewModel.logOut()

                                    }),
                            contentScale = ContentScale.Crop
                        )

                    }
                    Column {

                        Text(text = if (mainViewModel.login) mainViewModel.getNickName() else "点击头像登录&注册")
                        Text(text = if (mainViewModel.login) mainViewModel.getQQ() else "Guest")
                    }
                }
            }
        }
        item {
            Divider()
        }
        item {
            ListItem {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "主题更改")
                }
            }
        }
    }

}

@Composable
fun ListItem(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .height(150.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        content()
    }
}