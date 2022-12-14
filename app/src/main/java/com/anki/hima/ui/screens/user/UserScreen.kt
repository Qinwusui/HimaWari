package com.anki.hima.ui.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.twotone.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.anki.hima.ui.theme.deep_gray
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.TypeByUser
import com.anki.hima.viewmodel.MainViewModel
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(mainViewModel: MainViewModel, navController: NavController) {
    var userName by remember {
        mutableStateOf("")
    }
    var pwd by remember {
        mutableStateOf("")
    }
    var qq by remember {
        mutableStateOf("")
    }
    var isLogin by remember {
        mutableStateOf(false)
    }
    val qqRegex = "[1-9][0-9]{7,14}".toRegex()
    val pwdRegex = "\\w{6,10}".toRegex()
    var qqEnabled by remember {
        mutableStateOf(false)
    }
    var pwdEnabled by remember {
        mutableStateOf(false)
    }
    if (pwd.isNotEmpty()) {
        pwdEnabled = pwdRegex.matches(pwd)
    }
    if (qq.isNotEmpty()) {
        qqEnabled = qqRegex.matches(qq)
    }
    val login by mainViewModel.login.collectAsState()
    val signIn by mainViewModel.sign.collectAsState()
    val requestName by mainViewModel.name.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentColor = gray,
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = gray,
                actionIconContentColor = Color.White.copy(alpha = 0.7f),
                titleContentColor = Color.White
            ), navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.TwoTone.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            },
                title = {
                    Text(
                        text = if (!login) {
                            if (isLogin) {
                                "??????"
                            } else {
                                "??????"
                            }
                        } else {
                            "????????????"
                        }
                    )
                },
                actions = {
                    if (!login) {
                        Text(
                            text = if (isLogin) {
                                "???????"
                            } else {
                                "???????"
                            }, fontSize = 10.sp
                        )
                        IconButton(onClick = {
                            isLogin = !isLogin
                            if (login) {
                                isLogin = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.TwoTone.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (login) {
                mainViewModel.getQQ()
                mainViewModel.getNickName()
                Text("????????????$requestName")
            } else {
                qq = transTextField(typeByUser = TypeByUser.QQ, qqEnabled)
                if (!isLogin) {
                    userName = transTextField(typeByUser = TypeByUser.UserName, userName.length > 4)
                }
                pwd = transTextField(typeByUser = TypeByUser.Pwd, pwdEnabled)
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (!isLogin) {
                            if (qqEnabled && pwdEnabled) {
                                mainViewModel.signIn(userName, qq, pwd)
                            }
                        } else {
                            if (qqEnabled && pwdEnabled) {
                                mainViewModel.login(qq, pwd)
                            }
                        }
                    }) {
                    Text(text = if (isLogin) "??????" else "??????")
                }

            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun transTextField(
    typeByUser: TypeByUser,
    enabled: Boolean
): String {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    val labelFlow = flow {
        emit(
            if (text.isEmpty()) {
                when (typeByUser) {
                    TypeByUser.UserName -> "?????????"
                    TypeByUser.QQ -> "QQ"
                    TypeByUser.Pwd -> "??????"
                }
            } else {
                when {
                    typeByUser == TypeByUser.UserName && enabled -> "?????????"
                    typeByUser == TypeByUser.Pwd && enabled -> "??????"
                    typeByUser == TypeByUser.QQ && enabled -> "QQ"
                    !enabled -> when (typeByUser) {
                        TypeByUser.UserName -> "????????????4???????????????"
                        TypeByUser.QQ -> "QQ???8-12?????????"
                        TypeByUser.Pwd -> "???????????????6-10?????????????????????????????????"
                    }

                    else -> ""
                }
            }

        )
    }
    val labelText = labelFlow.collectAsState(initial = "")
    TextField(
        value = text,
        onValueChange = { text = it },
        label = {
            Text(
                text = labelText.value
            )
        },
        isError = !enabled && text.isNotEmpty(),
        visualTransformation = when (typeByUser) {
            TypeByUser.UserName, TypeByUser.QQ -> VisualTransformation.None
            TypeByUser.Pwd -> PasswordVisualTransformation()

        },
        keyboardOptions = when (typeByUser) {
            TypeByUser.UserName, TypeByUser.QQ -> {
                KeyboardOptions.Default
            }

            TypeByUser.Pwd -> {
                KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                )
            }

        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = deep_gray,
            containerColor = Color.Transparent,
            cursorColor = gray,
            placeholderColor = Color.Transparent,
        ),
        trailingIcon = {
            IconButton(onClick = { text = "" }) {
                Icon(imageVector = Icons.TwoTone.Close, contentDescription = null, tint = gray)
            }
        }
    )
    return text
}