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
import com.anki.hima.utils.bean.ResFriendList
import com.anki.hima.utils.bean.ResInfo
import com.anki.hima.utils.bean.VerifyInfo
import com.anki.hima.utils.bean.VerifyList
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

    private val _msgData = MutableStateFlow(MsgDataBase(null, "", "", "", "", ""))
    val msgData = _msgData.asStateFlow()

    //初始化操作
    init {
        autoLogin() //检查用户状态
        getGroupList() //读取群组列表
        initWebSocket()
        loadSimpleMsgList()
        getQQ()
        getNickName()
    }

    //连接到聊天室并且持续收取消息
    private fun initWebSocket() {
        viewModelScope.launch {
            repo.initWsSession()

            repo.receive().collect {
                _chatRoomMsgList.value.add(it)
                _msgData.value = it
                if (_simpleMsgList.value.size == 0) {
                    _simpleMsgList.value.add(
                        SimpleMsg(
                            it.msgIndex,
                            it.nickName,
                            it.time,
                            it.chatRoomId
                        )
                    )

                }
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
                _chatRoomMsgList.value = it.toMutableList()
            }
        }
    }

    /////////////////////////////////////用户发送消息//////////////////////////////////
    fun sendMsg(msg: String) {
        viewModelScope.launch {
            repo.sendMsg(_name.value, chatRoomId, _qq.value, msg)
        }
    }

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    fun getNickName() {
        viewModelScope.launch {
            repo.requestUname().collect {
                _name.value = it.msg
            }
        }
    }

    private val _qq = MutableStateFlow("")
    val qq = _qq.asStateFlow()

    //获取QQ
    fun getQQ() {
        viewModelScope.launch {
            repo.requestQQ().collect {
                _qq.value = it.msg
            }
        }
    }


    fun changeChatRoomId(chatRoomId: String) {
        this.chatRoomId = chatRoomId
    }

    ///////////////////////////////////////用户注册登录，用户自动登录//////////////////////////////

    private val _sign = MutableStateFlow(false)
    val sign = _sign.asStateFlow()
    private val _login = MutableStateFlow(false)
    val login = _login.asStateFlow()
    private fun autoLogin() {
        viewModelScope.launch {
            repo.autoLogin().collect {
                _login.value = it
            }
        }
    }

    fun login(qq: String, pwd: String) {
        viewModelScope.launch {
            repo.login(qq, pwd).collect {
                _login.value = it
                if (it) {
                    initWebSocket()
                }
            }
        }
    }

    fun signIn(uName: String, qq: String, pwd: String) {
        viewModelScope.launch {
            repo.signIn(uName, qq, pwd).collect {
                _sign.value = it
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            repo.logOut()
        }
        _login.value = false
        _sign.value = false
    }

    ///////////////////////////////////////////////////////////群组列表/////////////////////////////////////////////////////////////////
    //读取群组列表
    private fun getGroupList() = viewModelScope.launch {
        repo.getGroupList().collect {
            _groupList.value = it
        }
    }
    ///////////////////////////////////////////////////////怎么解决用户离线后消息缓存？//////////////////////////////////////////

    ///////////////////////////////////////////////////////获取好友列表//////////////////////////////////////////////////////
    private val _friendsList = MutableStateFlow(ResFriendList())
    val friendList = _friendsList.asStateFlow()

    //获取好友列表
    fun queryFriendList(str: String) {
        viewModelScope.launch {
            if (str != "") {
                repo.queryFriends(str).collect {
                    _friendsList.value = it
                }
            }
        }
    }

    private val _verifyList = MutableStateFlow(VerifyList())
    val verifyList = _verifyList.asStateFlow()
    fun queryVerifyList(qq: String) {
        viewModelScope.launch {
            repo.queryVerifyMsg(qq).collect {
                _verifyList.value = it
            }
        }
    }

    private val _verifyMsg = MutableStateFlow(ResInfo())
    val verifyMsg = _verifyMsg.asStateFlow()

    //发送添加好友请求
    fun sendAddFriendMsg(from: String, to: String, verifyMsg: String) {
        viewModelScope.launch {
            repo.addFriend(from, to, verifyMsg).collect {
                _verifyMsg.value = it
            }
        }
    }

    private val _respVerifyMsg = MutableStateFlow(ResInfo())
    val respVerifyResp = _respVerifyMsg.asStateFlow()

    //回应验证消息
    fun respVerifyMsg(verifyInfo: VerifyInfo, admit: Boolean) {
        viewModelScope.launch {
            repo.respVerifyMsg(verifyInfo, admit).collect {
                _respVerifyMsg.value = it
            }
        }
    }

    //模糊查询用户
    private val _respSearchList = MutableStateFlow(ResFriendList())
    val respSearchList = _respSearchList.asStateFlow()
    fun querySearchUserList(qq: String) {
        viewModelScope.launch {
            repo.querySearchList(qq).collect {
                _respSearchList.value = it
            }
        }
    }
}