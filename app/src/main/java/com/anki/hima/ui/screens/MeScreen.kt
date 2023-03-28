package com.anki.hima.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.anki.hima.viewmodel.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MeScreen(mainViewModel: MainViewModel, navController: NavController) {
    val listState = rememberLazyListState()
    val login by mainViewModel.login.collectAsState()
    val nickname by mainViewModel.name.collectAsState()
    val userId by mainViewModel.userId.collectAsState()

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        item {
            ListItem {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .combinedClickable(
                            onClick = {
                                navController.navigate(NavScreen.UserView.route)
                            },
                            onLongClick = {
                                mainViewModel.logOut()
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (login) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    if (login) {
                        AsyncImage(
                            model = "http://q2.qlogo.cn/headimg_dl?dst_uin=$userId&spec=100",
                            contentDescription = null
                        )
                        Column {
                            Text(text = nickname)
                            Text(text = "$userId")
                        }
                    } else {
                        TextButton(onClick = {
                            navController.navigate(NavScreen.UserView.route)

                        }) {
                            Text(text = "点击注册&登录")
                        }
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