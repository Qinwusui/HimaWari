package com.anki.hima.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anki.hima.R
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.dao.SimpleMsg
import com.anki.hima.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnrememberedMutableState")
@ExperimentalMaterial3Api
@Composable
fun MessageScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val scp = rememberCoroutineScope()
    val msg by mainViewModel.msgData.collectAsState()
    val msgList by mainViewModel.simpleMsgList.collectAsState()
    val m by rememberUpdatedState(newValue = msg)
    val listState = rememberLazyListState()
    DisposableEffect(m) {
        onDispose {
            scp.launch {
                listState.animateScrollToItem(msgList.size)
            }
        }
    }
    Scaffold {
        LazyColumn(state = listState, contentPadding = it) {
            itemsIndexed(msgList) { i: Int, item: SimpleMsg ->
                var popMenu by remember {
                    mutableStateOf(false)
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                mainViewModel.changeChatRoomId(item.chatRoomId)
                                navController.navigate(NavScreen.ChatView.route)
                            },
                            onLongClick = {
                                popMenu = !popMenu
                            }
                        )) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, bottom = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = if (popMenu) Arrangement.SpaceBetween else Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                Card(shape = RoundedCornerShape(40.dp)) {
                                    AsyncImage(
                                        model = R.drawable.ic_launcher_foreground,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(gray.copy(alpha = 0.4f)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(text = item.chatRoomId, fontSize = 20.sp)
                                    Text(
                                        text = item.nickName,
                                        fontSize = 14.sp,
                                        color = Color.Black.copy(0.4f)
                                    )
                                }
                            }
                            if (popMenu) {
                                Spacer(modifier = Modifier.width(10.dp))
                                TextButton(onClick = { /*TODO*/ }) {
                                    Text(text = "删除")
                                }
                            }
                        }

                    }

                    if (i != msgList.size - 1) {
                        Divider(
                            modifier = Modifier.padding(start = 60.dp, end = 10.dp),
                            color = gray.copy(alpha = 0.2f)
                        )
                    }
                }

            }
        }
    }

}