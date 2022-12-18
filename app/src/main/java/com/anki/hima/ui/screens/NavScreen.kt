package com.anki.hima.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.anki.hima.R
import com.anki.hima.ui.HorizontalPages
import com.anki.hima.ui.theme.deep_gray
import com.anki.hima.ui.theme.gray
import com.anki.hima.viewmodel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

data class BottomItem(
    val icon: Int,
    val horizontalPages: HorizontalPages,
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun NavScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setSystemBarsColor(gray, false)
    })

    val navList = listOf(
        BottomItem(R.drawable.ic_message, HorizontalPages.MessageHorizontalPages),
        BottomItem(R.drawable.ic_contact, HorizontalPages.ContactHorizontalPages),
        BottomItem(R.drawable.ic_group, HorizontalPages.GroupHorizontalPages),
        BottomItem(R.drawable.ic_about, HorizontalPages.MeHorizontalPages)
    )
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()


    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = navList[pagerState.currentPage].horizontalPages.title,
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = gray),
        )
    }, bottomBar = {
        BottomAppBar(modifier = Modifier.fillMaxWidth(), containerColor = Color.Transparent) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                navList.forEachIndexed { i: Int, item: BottomItem ->
                    val iconSize by animateDpAsState(
                        targetValue =
                        if (pagerState.currentPage == i) 25.dp else 20.dp
                    )
                    val iconColor by animateColorAsState(
                        targetValue =
                        if (pagerState.currentPage == i) deep_gray else gray
                    )
                    Card(
                        shape = RoundedCornerShape(50.dp), colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(i)
                                    }
                                },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(iconSize)
                            )
                            Text(
                                text = item.horizontalPages.title, style = TextStyle(
                                    color = deep_gray, fontSize = 10.sp
                                )
                            )
                        }
                    }

                }

            }
        }
    }) { pad ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            contentPadding = pad,
            count = navList.size,

            ) { page ->
            when (page) {
                0 -> MessageScreen(mainViewModel, navController)
                1 -> ContactScreen(mainViewModel, navController)
                2 -> GroupScreen(mainViewModel, navController)
                3 -> MeScreen(mainViewModel, navController)
            }
        }

    }
}

sealed class NavScreen(val route: String) {
    object MainView : NavScreen("mainView")
    object ChatView : NavScreen("chatView")
    object UserView : NavScreen("userView")
    object UserSearchView : NavScreen("userSearchView")
}