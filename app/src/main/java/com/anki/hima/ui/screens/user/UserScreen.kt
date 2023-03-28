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
import com.anki.hima.utils.loge
import com.anki.hima.utils.toastShort
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
    var userId by remember {
        mutableStateOf("")
    }
    var isLogin by remember {
        mutableStateOf(false)
    }

    val idRegex = "\\d{1,14}".toRegex()
    val pwdRegex = "[\\w\\d.@]{6,14}".toRegex()
    val userNameRegex = "\\w{6,14}".toRegex()
    var idEnabled by remember {
        mutableStateOf(false)
    }
    var userNameEnabled by remember {
        mutableStateOf(false)
    }
    var pwdEnabled by remember {
        mutableStateOf(false)
    }
    if (pwd.isNotEmpty()) {
        pwdEnabled = pwdRegex.matches(pwd)
    }
    if (userId.isNotEmpty()) {
        idEnabled = idRegex.matches(userId)
    }
    if (userName.isNotEmpty()) {
        userNameEnabled = userNameRegex.matches(userName)
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
                                "登录"
                            } else {
                                "注册"
                            }
                        } else {
                            "您已登录"
                        }
                    )
                },
                actions = {
                    if (!login) {
                        Text(
                            text = if (isLogin) {
                                "注册?"
                            } else {
                                "登录?"
                            }, fontSize = 10.sp
                        )
                        IconButton(onClick = {
                            isLogin = !isLogin

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
                //TODO 登录
                Text("欢迎你，$requestName")
            } else {

                if (!isLogin) {
                    userName = transTextField(typeByUser = TypeByUser.UserName, userName.length > 4)

                } else {
                    userId = transTextField(typeByUser = TypeByUser.UserId, idEnabled, userId)
                }
                pwd = transTextField(typeByUser = TypeByUser.Pwd, pwdEnabled)
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (!isLogin) {

                            if (userNameEnabled && pwdEnabled) {
                                mainViewModel.signIn(userName, pwd)
                            }

                        } else {
                            if (idEnabled && pwdEnabled) {
                                val id = userId.toIntOrNull()
                                if (id == null) {
                                    "请输入正确的ID".toastShort()
                                } else {
                                    mainViewModel.login(id, pwd)
                                }
                            }
                        }
                    }) {
                    Text(text = if (isLogin) "登录" else "注册")
                }

            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun transTextField(
    typeByUser: TypeByUser,
    enabled: Boolean,
    userId: String = "",
): String {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    val labelFlow = flow {
        emit(
            if (text.isEmpty()) {
                when (typeByUser) {
                    TypeByUser.UserName -> "用户名"
                    TypeByUser.UserId -> "用户ID"
                    TypeByUser.Pwd -> "密码"
                }
            } else {
                when {
                    typeByUser == TypeByUser.UserName && enabled -> "用户名"
                    typeByUser == TypeByUser.Pwd && enabled -> "密码"
                    typeByUser == TypeByUser.UserId && enabled -> "用户ID"
                    !enabled -> when (typeByUser) {
                        TypeByUser.UserName -> "用户名为6位以上字符"
                        TypeByUser.UserId -> "ID为1-14位数字"
                        TypeByUser.Pwd -> "密码需包含6-14位数字字符"
                    }

                    else -> ""
                }
            }

        )
    }
    val labelText = labelFlow.collectAsState(initial = "")
    TextField(
        value = if (userId != "") userId else text,
        onValueChange = { text = it },
        label = {
            Text(
                text = labelText.value
            )
        },
        isError = !enabled && text.isNotEmpty(),
        visualTransformation = when (typeByUser) {
            TypeByUser.UserName, TypeByUser.UserId -> VisualTransformation.None
            TypeByUser.Pwd -> PasswordVisualTransformation()
        },
        keyboardOptions = when (typeByUser) {
            TypeByUser.UserName, TypeByUser.UserId -> {
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
            focusedTextColor = deep_gray,
            containerColor = Color.Transparent,
            cursorColor = gray,
            focusedPlaceholderColor = Color.Transparent,
        ),
        trailingIcon = {
            IconButton(onClick = { text = "" }) {
                Icon(imageVector = Icons.TwoTone.Close, contentDescription = null, tint = gray)
            }
        }
    )
    return text
}