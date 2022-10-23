package com.anki.hima.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anki.hima.utils.MsgListManager
import com.anki.hima.utils.Repository
import com.anki.hima.utils.bean.Group
import com.anki.hima.utils.bean.GroupList
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.dao.SimpleMsg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
class MainViewModel(private val repo: Repository = Repository) : ViewModel() {
    /**
     * 承载业务：
     *
     *      - 1.建立Websocket连接 [initWebSocket]
     *      - 2.从消息数据库中读取消息列表的数据 [readMsgFromDb]
     *      - 3.从消息数据库中读取某个聊天室的数据[chatRoomId] [readChatRoomIdMsg]
     *      - 4.获取群组列表 [fetchGroupList]
     *      - 5.实现自动登录与用户名密码登录 [login] [autoLogin] [sign]
     */


    //群组列表
    private val _groupList =
        MutableStateFlow(GroupList(mutableListOf(Group("Loading...", "0"))))
    val groupList = _groupList.asStateFlow()

    //消息列表
    private val _simpleMsgList = MutableStateFlow(mutableListOf<SimpleMsg>())
    val simpleMsgList = _simpleMsgList.asStateFlow()


    var chatRoomId by mutableStateOf("0")
        private set


    private val _chatRoomMsgList = MutableStateFlow(mutableListOf<MsgDataBase>())
    val chatRoomMsgList = _chatRoomMsgList.asStateFlow()

    var signData by mutableStateOf("")
    var loginData by mutableStateOf("")

    var msgData = MutableStateFlow(MsgDataBase(null, "", "", "", "", ""))
        private set

    //初始化操作
    init {
        autoLogin() //检查用户状态
        getGroupList() //读取群组列表
        initWebSocket()
        loadSimpleMsgList()
    }

    //连接到聊天室并且持续收取消息
    private fun initWebSocket() {
        viewModelScope.launch {
            repo.initWsSession()
            repo.receive().collect {
                _chatRoomMsgList.value.add(it)
                msgData.value = it
            }
        }
    }

    private fun loadSimpleMsgList() {
        viewModelScope.launch {
            MsgListManager.getSimpleMsgList().collect {
                _simpleMsgList.value = it.toMutableList()
            }
        }
    }

    fun loadMsgList() {
        viewModelScope.launch {
            MsgListManager.getMsgList(chatRoomId).collect {
                _chatRoomMsgList.value = mutableListOf()
                _chatRoomMsgList.value = it.toMutableList()
            }
        }
    }

    /////////////////////////////////////用户发送消息//////////////////////////////////
    fun sendMsg(msg: String) {
        viewModelScope.launch {
            repo.sendMsg(getNickName(), chatRoomId, getQQ(), msg)
        }
    }

    fun getNickName() = repo.getInfo(0)
    fun getQQ() = repo.getInfo(1)

    fun changeChatRoomId(chatRoomId: String) {
        this.chatRoomId = chatRoomId
    }

    ///////////////////////////////////////用户注册登录，用户自动登录//////////////////////////////

    var sign by mutableStateOf(false)
        private set
    var login by mutableStateOf(false)
        private set

    private fun autoLogin() {
        viewModelScope.launch {
            repo.autoLogin().collect {
                login = it
            }
        }
    }

    fun login(uName: String, qq: String, pwd: String) {
        viewModelScope.launch {
            repo.login(uName, qq, pwd).collect {
                login = it
                if (it) {
                    repo.initWsSession()
                }
            }
        }
    }

    fun signIn(uName: String, qq: String, pwd: String) {
        viewModelScope.launch {
            repo.signIn(uName, qq, pwd).collect {
                sign = it
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            repo.logOut()
        }
        login = false

    }

    ///////////////////////////////////////////////////////////群组列表/////////////////////////////////////////////////////////////////
    //读取群组列表
    private fun getGroupList() = viewModelScope.launch {
        repo.getGroupList().collect {
            _groupList.value = it
        }
    }
}