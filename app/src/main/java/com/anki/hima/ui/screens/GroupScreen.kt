package com.anki.hima.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anki.hima.viewmodel.MainViewModel

@Composable
fun GroupScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val groupList by mainViewModel.groupList.collectAsState()
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        itemsIndexed(groupList.info ?: mutableListOf()) { _, group ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        mainViewModel.changeGroup(group.id, groupName = group.groupName)
                        navController.navigate(NavScreen.ChatView.route)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
//                    AsyncImage(
//                        model = group.groupIconUrl,
//                        contentDescription = null,
//                        modifier = Modifier.size(40.dp)
//                    )
                    Text(
                        text = group.groupName,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive,
                        color = Color.Black.copy(alpha = 0.6f),
                        textAlign = TextAlign.End
                    )
                }

            }
        }
    }
}