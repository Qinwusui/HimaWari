package com.anki.hima.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import com.anki.hima.utils.bean.FriendApply
import com.anki.hima.utils.bean.FriendData
import com.anki.hima.utils.loge
import com.anki.hima.utils.toastShort
import com.anki.hima.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val listState = rememberLazyListState()
    val friendList by mainViewModel.friendList.collectAsState()
    rememberUpdatedState(newValue = friendList).value
    val verifyList by mainViewModel.verifyList.collectAsState()
    rememberUpdatedState(newValue = verifyList).value
    val userId by mainViewModel.userId.collectAsState()
    val id by rememberUpdatedState(newValue = userId)
    val respVerifyResp by mainViewModel.respVerifyResp.collectAsState()
    rememberUpdatedState(newValue = respVerifyResp).value
    val login by mainViewModel.login.collectAsState()
    rememberUpdatedState(newValue = login)


    if (login) {
        mainViewModel.queryFriendList()
        mainViewModel.queryApplyList(id)
    }
    var showDialog by remember {

        mutableStateOf(false)
    }
    var friendApply by remember {
        mutableStateOf(FriendApply(0, 0, ""))
    }
    AnimatedVisibility(visible = showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = {
            Text(text = "验证信息")
        }, confirmButton = {
            TextButton(onClick = {
                mainViewModel.opApply(friendId = friendApply.from, 1)
                showDialog = false
            }) {
                Text(text = "同意")
            }
        }, dismissButton = {
            TextButton(onClick = {
                mainViewModel.opApply(friendId = friendApply.from, 2)
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
            itemsIndexed(verifyList.info ?: mutableListOf()) { _: Int, item: FriendApply ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        friendApply = item
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
                            Text(text = "${item.from}")
                            Text(text = "验证消息：${item.msg}")
                        }
                    }

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
            if ((friendList.info ?: mutableListOf()).isEmpty()) {
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
                itemsIndexed(friendList.info ?: mutableListOf()) { _: Int, item: FriendData ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .combinedClickable(
                                onClick = {
                                    //点击进入聊天页面
                                    mainViewModel.changeFriendId(item)
                                    item
                                        .toString()
                                        .loge()
                                    navController.navigate(NavScreen.ChatView.route)
                                },
                                onLongClick = {
                                    //TODO 删除好友对话框
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = "https://q2.qlogo.cn/headimg_dl?dst_uin=${item.friendId}&spec=100",
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "${item.friendName}")
                            Text(text = "${item.friendId}")

                        }
                    }
                    Divider()
                }
            }
        }
    }
}