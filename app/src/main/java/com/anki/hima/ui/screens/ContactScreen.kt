package com.anki.hima.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anki.hima.ui.theme.deep_gray
import com.anki.hima.utils.bean.FriendItem
import com.anki.hima.utils.bean.VerifyInfo
import com.anki.hima.utils.toastShort
import com.anki.hima.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val listState = rememberLazyListState()
    val friendList by mainViewModel.friendList.collectAsState()
    rememberUpdatedState(newValue = friendList).value
    val verifyList by mainViewModel.verifyList.collectAsState()
    rememberUpdatedState(newValue = verifyList).value
    val _qq by mainViewModel.qq.collectAsState()
    val qq by rememberUpdatedState(newValue = _qq)
    val respVerifyResp by mainViewModel.respVerifyResp.collectAsState()
    rememberUpdatedState(newValue = respVerifyResp).value
    val login by mainViewModel.login.collectAsState()
    rememberUpdatedState(newValue = login)


    if (login) {
        mainViewModel.queryFriendList(qq)
        mainViewModel.queryVerifyList(qq)
    }
    var showDialog by remember {

        mutableStateOf(false)
    }
    var verifyInfo by remember {
        mutableStateOf(VerifyInfo("", "", "", ""))
    }
    AnimatedVisibility(visible = showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = {
            Text(text = "验证信息")
        }, confirmButton = {
            TextButton(onClick = {
                mainViewModel.respVerifyMsg(verifyInfo = verifyInfo, admit = true)
                showDialog = false
            }) {
                Text(text = "同意")
            }
        }, dismissButton = {
            TextButton(onClick = {
                mainViewModel.respVerifyMsg(verifyInfo = verifyInfo, admit = false)
                showDialog = false
            }) {
                Text(text = "拒绝")
            }
        })
    }
    Scaffold(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = it,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            //新朋友
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    TextButton(onClick = {
                        "看看哪些新朋友来找你吧~".toastShort()
                    }) {
                        Text(text = "新朋友")
                    }
                }
            }

            //验证消息列表
            itemsIndexed(verifyList.list) { _: Int, item: VerifyInfo ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        verifyInfo = item
                        showDialog = true
                    }
                    .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = "https://q2.qlogo.cn/headimg_dl?dst_uin=${item.from}&spec=100",
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier
                                .height(60.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = item.from)
                            Text(text = "验证消息：${item.verifyMsg}")
                        }
                    }
                    Text(
                        text = item.submitTime,
                        fontSize = 10.sp,
                        style = TextStyle.Default.copy(
                            color = deep_gray.copy(alpha = 0.7f)
                        )
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(onClick = {
                        navController.navigate(NavScreen.UserSearchView.route)
                    }) {
                        Text(text = "添加好友？")
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    TextButton(onClick = {
                        "好朋友~".toastShort()
                    }) {
                        Text(text = "好友")
                    }
                }
            }
            //好友列表
            if (friendList.list.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "没有好友呢~")
                    }
                }
            } else {
                itemsIndexed(friendList.list) { _: Int, item: FriendItem ->

                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = "https://q2.qlogo.cn/headimg_dl?dst_uin=${item.qq}&spec=100",
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    //TODO 好友聊天

                                }
                                .height(60.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = item.uName)
                            Text(text = item.qq)

                        }
                    }
                    Divider()
                }
            }
        }
    }
}