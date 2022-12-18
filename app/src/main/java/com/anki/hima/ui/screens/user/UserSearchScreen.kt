package com.anki.hima.ui.screens.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anki.hima.ui.theme.deep_gray
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.toastShort
import com.anki.hima.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(mainViewModel: MainViewModel, navController: NavController) {
    val _searchList by mainViewModel.respSearchList.collectAsState()
    val searchList by rememberUpdatedState(newValue = _searchList)
    var input by remember {
        mutableStateOf("")
    }
    val _qq by mainViewModel.qq.collectAsState()
    val qq by rememberUpdatedState(newValue = _qq)

    var verifyInfo by remember {
        mutableStateOf("")
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var toQQ by remember {
        mutableStateOf("")
    }
    AnimatedVisibility(visible = showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "输入验证信息")
            },
            text = {
                Column {
                    Text(text = "验证信息将展示给对方")
                    Spacer(modifier = Modifier.height(20.dp))
                    TextField(
                        value = verifyInfo,
                        onValueChange = { verifyInfo = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        label = {
                            Text(text = "验证信息")
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = deep_gray,
                            focusedIndicatorColor = deep_gray,
                            unfocusedIndicatorColor = deep_gray,
                            unfocusedLabelColor = deep_gray,
                            containerColor = Color.Transparent,
                            cursorColor = deep_gray,
                            focusedLabelColor = deep_gray,
                            placeholderColor = deep_gray
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (verifyInfo.isEmpty()) {
                        "填点东西好让别人知道你是不是卖茶叶的嘛".toastShort()
                    } else {
                        mainViewModel.sendAddFriendMsg(qq, toQQ, verifyInfo)

                        showDialog = false
                    }
                }) {
                    Text(text = "发送")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text(text = "取消")
                }
            })
    }
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = gray,
            titleContentColor = Color.White
        ), title = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.TwoTone.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
                Text(text = "搜搜")
                IconButton(onClick = { mainViewModel.querySearchUserList(input) }) {
                    Icon(
                        imageVector = Icons.TwoTone.Search,
                        contentDescription = null,
                        tint = Color.White.copy(0.7f)
                    )
                }
            }
        })
    }) {
        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = it,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            item {

                TextField(
                    value = input,
                    onValueChange = { str ->
                        input = str
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "输入qq或用户名进行模糊查询")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = deep_gray,
                        focusedIndicatorColor = deep_gray,
                        unfocusedIndicatorColor = deep_gray,
                        unfocusedLabelColor = deep_gray,
                        containerColor = Color.Transparent,
                        cursorColor = deep_gray,
                        focusedLabelColor = deep_gray,
                        placeholderColor = deep_gray
                    )
                )
            }
            if (searchList.list.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "用户名")
                        Text(text = "QQ")
                    }
                }
                itemsIndexed(searchList.list) { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                toQQ = item.qq
                                if (item.qq == qq) {
                                    "不能添加自己哦~".toastShort()
                                } else {
                                    showDialog = true
                                }
                            }
                            .height(50.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.uName)
                        Text(text = item.qq)
                    }
                }
            }

        }
    }
}