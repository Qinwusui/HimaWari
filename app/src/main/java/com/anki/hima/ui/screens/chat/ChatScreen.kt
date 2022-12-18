package com.anki.hima.ui.screens.chat

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anki.hima.ui.theme.deep2_red
import com.anki.hima.ui.theme.deep_red
import com.anki.hima.ui.theme.gray
import com.anki.hima.utils.toastShort
import com.anki.hima.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.john.waveview.WaveView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@SuppressLint(
    "UnrememberedMutableState",
    "CoroutineCreationDuringComposition",
    "MutableCollectionMutableState"
)
@ExperimentalMaterial3Api
@Composable

fun ChatScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController,
) {
    mainViewModel.loadMsgList()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequest = FocusRequester()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val nickname by mainViewModel.name.collectAsState()
    val qq by mainViewModel.qq.collectAsState()
    val chatRoomMsgList by mainViewModel.chatRoomMsgList.collectAsState()
    val msg by mainViewModel.msgData.collectAsState()
    rememberUpdatedState(newValue = msg).value
    val chatRoomMsgData by rememberUpdatedState(newValue = chatRoomMsgList)

    val login by mainViewModel.login.collectAsState()
    var emojiClicked by rememberSaveable {
        mutableStateOf(false)
    }

    scope.launch {
        listState.animateScrollToItem(chatRoomMsgData.size)
    }
    SideEffect {
        systemUiController.setSystemBarsColor(gray, false)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    var inputHasFocus by remember {
        mutableStateOf(false)
    }
    BottomSheetScaffold(
        sheetPeekHeight = 50.dp,
        //抽屉布局 存放表情
        sheetContent = {
            Column(
                modifier = Modifier
                    .height(250.dp)
                    .background(gray, RoundedCornerShape(10.dp))
            ) {
                var input by remember {
                    mutableStateOf("")
                }
                Column {
                    //输入控件
                    Row(
                        Modifier
                            .height(50.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicTextField(modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequest)
                                .padding(horizontal = 10.dp)
                                .onFocusChanged {
                                    inputHasFocus = it.hasFocus
                                },
                                textStyle = TextStyle(
                                    color = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (login) {
                                            if (input == "") {
                                                "说点什么吧...".toastShort()
                                                return@KeyboardActions
                                            }
                                            mainViewModel.sendMsg(msg = input)
                                            input = ""
                                        } else {
                                            "你还没有登录呢...".toastShort()
                                        }
                                    }
                                ),
                                value = input,
                                onValueChange = { input = it },
                                cursorBrush = Brush.Companion.linearGradient(
                                    colors = listOf(
                                        Color.White,
                                        deep2_red
                                    ), tileMode = TileMode.Decal
                                ),
                                decorationBox = { innerTextField ->
                                    Row(
                                        modifier = Modifier.padding(start = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        if (!inputHasFocus) {
                                            Text(
                                                text = "写点什么吧...", style = TextStyle(
                                                    color = Color.White.copy(alpha = 0.5f)
                                                )
                                            )
                                        } else {
                                            innerTextField()
                                        }
                                        Row {
                                            //清除所有文字按钮
                                            if (input.isNotEmpty()) {
                                                IconButton(onClick = { input = "" }) {
                                                    Icon(
                                                        imageVector = Icons.TwoTone.Close,
                                                        tint = Color.White.copy(alpha = 0.7f),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                            //表情按钮
                                            IconButton(onClick = {
                                                emojiClicked = !emojiClicked
                                                scope.launch {
                                                    if (emojiClicked) {
                                                        scaffoldState.bottomSheetState.expand()
                                                    } else {
                                                        if (inputHasFocus) {
                                                            focusRequest.freeFocus()
                                                        }
                                                        keyboardController?.hide()
                                                        scaffoldState.bottomSheetState.collapse()
                                                    }
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.TwoTone.Face,
                                                    tint = Color.White.copy(alpha = 0.7f),
                                                    contentDescription = null
                                                )
                                            }

                                            //发送按钮
                                            IconButton(onClick = {
                                                if (login) {
                                                    if (input == "") {
                                                        "说点什么吧...".toastShort()
                                                        return@IconButton
                                                    }
                                                    mainViewModel.sendMsg(msg = input)
                                                    input = ""
                                                } else {
                                                    "你还没有登录呢...".toastShort()
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Send,
                                                    contentDescription = null,
                                                    tint = Color.White.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }


                                })
                        }
                    }

                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidView(factory = { c ->
                            return@AndroidView WaveView(c, null).apply {
                                setProgress(50)
                            }
                        })
                        Column {
                            CircularProgressIndicator(modifier = Modifier.size(50.dp), color = gray)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "还在赶工...", color = Color.White)
                        }
                    }
                }

            }
        },
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(mainViewModel.chatRoomId, color = Color.White)
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }, actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = gray
            )
            )
        },
    ) {
        mainViewModel.getNickName()
        mainViewModel.getQQ()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            itemsIndexed(chatRoomMsgData) { _, x ->

                val ori = x.nickName != nickname
                val shape = if (ori) {
                    RoundedCornerShape(
                        10.dp, 10.dp, 10.dp, 3.dp
                    )
                } else {
                    RoundedCornerShape(
                        10.dp, 10.dp, 3.dp, 10.dp
                    )
                }
                val popColor = if (ori) {
                    gray
                } else {
                    deep_red
                }
                val arr = if (ori) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                }
                val horizontalAlignment = if (ori) {
                    Alignment.Start
                } else {
                    Alignment.End
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            start = 10.dp,
                            end = 10.dp
                        )
                ) {

                    //消息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = arr,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (ori) {
                            Card(shape = RoundedCornerShape(50.dp)) {
                                AsyncImage(
                                    model = "https://q2.qlogo.cn/headimg_dl?dst_uin=${x.qq}&spec=100",
                                    contentDescription = null
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            horizontalAlignment = horizontalAlignment
                        ) {
                            //昵称展示
                            Text(

                                text = x.nickName,
                                color = Color.Black.copy(alpha = 0.6f),
                                textAlign = if (ori) TextAlign.Start else TextAlign.End
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        popColor, shape
                                    )
                                    .align(if (ori) Alignment.Start else Alignment.End),
                            ) {
                                Text(
                                    text = x.msg,
                                    modifier = Modifier.padding(10.dp),
                                    color = Color.White
                                )
                            }

                        }
                        if (!ori) {
                            Card(shape = RoundedCornerShape(50.dp)) {
                                AsyncImage(
                                    model = "https://q2.qlogo.cn/headimg_dl?dst_uin=$qq&spec=100",
                                    contentDescription = null
                                )
                            }
                        }
                    }
//                        Surface(
//                            shape = shape,
//                            color = color,
//                            modifier = Modifier
//                                .padding(horizontal = 10.dp)
//                                .align(align)
//                        ) {
//
//                        }

                }


            }
        }
    }
    BackHandler {
        if (scaffoldState.bottomSheetState.isExpanded) {
            focusRequest.freeFocus()
            keyboardController?.hide()
            scope.launch {
                scaffoldState.bottomSheetState.collapse()
            }
        } else {
            navController.popBackStack()
        }
    }

}