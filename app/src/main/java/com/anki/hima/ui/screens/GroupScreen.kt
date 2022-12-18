package com.anki.hima.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.anki.hima.viewmodel.MainViewModel

@Composable
fun GroupScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val groupList by mainViewModel.groupList.collectAsState()
    LazyColumn(state = listState) {
        itemsIndexed(groupList.list) { _, group ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(10.dp)
                    .clickable {
                        mainViewModel.changeChatRoomId(group.groupId)
                        navController.navigate(NavScreen.ChatView.route)
                    }
            ) {

                Text(
                    text = group.groupName,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Cursive,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}