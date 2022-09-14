package com.anki.hima.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.loge
import com.anki.hima.utils.toastShort
import com.anki.hima.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(mainViewModel: MainViewModel = viewModel()) {
    val listState = rememberLazyListState()
    val sign by mainViewModel.sign.collectAsState()

    val login by mainViewModel.login.collectAsState()
    var uName by remember {
        mutableStateOf("")
    }
    var qq by remember {
        mutableStateOf("")
    }
    var pwd by remember {
        mutableStateOf("")
    }
    var needLogin by remember {
        mutableStateOf(false)
    }
    var needSignIn by remember {
        mutableStateOf(false)
    }
    if (sign) {
        uName = ""
        pwd = ""
        qq = ""
        needSignIn = false
    }

    if (login) {
        needLogin = false
        uName = ""
        pwd = ""
        qq = ""
    }

    AnimatedVisibility(visible = needLogin) {
        AlertDialog(
            onDismissRequest = { needSignIn = false },
            containerColor = gray,
            title = {
                Text(text = "SignIn", color = Color.White)
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
                            Text(text = "UserName", color = Color.White.copy(alpha = 0.4f))
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
                            Text(text = "Password", color = Color.White.copy(alpha = 0.4f))
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

                    TextButton(onClick = {
                        mainViewModel.login(uName, pwd)
                        needLogin = false
                    }) {
                        Text(text = "Login", color = Color.White)
                    }
                }

            },
            dismissButton = {
                TextButton(onClick = {
                    needLogin = false
                }) {
                    Text(text = "Cancel")
                }
            })
    }
    AnimatedVisibility(visible = needSignIn) {

        AlertDialog(
            onDismissRequest = { needSignIn = false },
            containerColor = gray,
            title = {
                Text(text = "SignIn", color = Color.White)
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
                            Text(text = "UserName", color = Color.White.copy(alpha = 0.4f))
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
                            Text(text = "QQ", color = Color.White.copy(alpha = 0.4f))
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
                            Text(text = "Password", color = Color.White.copy(alpha = 0.4f))
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
                        Text(text = "Cancel", color = Color.White)
                    }
                    TextButton(onClick = {
                        mainViewModel.signIn(uName, qq, pwd)
                    }) {
                        Text(text = "Sign In", color = Color.White)
                    }
                }

            },
            dismissButton = {
                TextButton(onClick = {
                    needSignIn = false
                    needLogin = true
                }) {
                    Text(text = "Login")
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
                            .size(100.dp)
                            .clickable {
                                if (login) {
                                    "你已登录".toastShort()
                                } else {
                                    needSignIn = true
                                }
                            }, shape = RoundedCornerShape(50.dp)
                    ) {
                        AsyncImage(
                            model = "https://q2.qlogo.cn/headimg_dl?dst_uin=${
                                mainViewModel.getUserInfo(
                                    true
                                )
                            }&spec=100",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
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