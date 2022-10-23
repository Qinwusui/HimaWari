package com.anki.hima.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anki.hima.R
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.dao.SimpleMsg
import com.anki.hima.viewmodel.MainViewModel

@SuppressLint("UnrememberedMutableState")
@ExperimentalMaterial3Api
@Composable
fun MessageScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val msgList by mainViewModel.simpleMsgList.collectAsState()
    val _msgData by mainViewModel.msgData.collectAsState()
    val msgData by rememberUpdatedState(newValue = _msgData)
    val _msgList by rememberUpdatedState(newValue = msgList)
    Scaffold {
        LazyColumn(state = listState, contentPadding = it) {
            itemsIndexed(_msgList) { i: Int, item: SimpleMsg ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            mainViewModel.changeChatRoomId(item.chatRoomId)
                            navController.navigate(NavScreen.ChatView.route)
                        }) {
                    Column(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(shape = RoundedCornerShape(50.dp)) {
                                AsyncImage(
                                    model = R.drawable.ic_launcher_foreground,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(gray.copy(alpha = 0.4f)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(text = item.chatRoomId, fontSize = 20.sp)
                                Text(
                                    text = item.nickName,
                                    fontSize = 15.sp,
                                    color = Color.Black.copy(0.4f)
                                )
                            }
                        }

                    }
                    if (i != _msgList.size - 1) {
                        Divider()
                    }
                }

            }
        }
    }

}