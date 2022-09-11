package com.anki.hima.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anki.hima.ui.activity.ChatActivity
import com.anki.hima.utils.Repository
import com.anki.hima.viewmodel.MainViewModel

@ExperimentalMaterial3Api
@Composable
fun MessageScreen(mainViewModel: MainViewModel = viewModel()) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val messageScreenDataList = mutableListOf<Repository.MessageScreenData>()
    for (i in 0 until 10) {
        messageScreenDataList.add(
            Repository.MessageScreenData(
                name = "好友$i",
                simpleText = "发来一条消息",
                iconUrl = "https://q2.qlogo.cn/headimg_dl?dst_uin=2064508450&spec=100"
            )
        )
    }
    Scaffold(floatingActionButton = {}, floatingActionButtonPosition = FabPosition.End) {
        LazyColumn(state = listState, contentPadding = it) {
            itemsIndexed(messageScreenDataList) { i: Int, item: Repository.MessageScreenData ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(context, ChatActivity::class.java)
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = item.name, fontSize = 20.sp)
                        Text(
                            text = item.simpleText,
                            fontSize = 15.sp,
                            color = Color.Black.copy(0.4f)
                        )
                    }

                }
            }
        }
    }

}