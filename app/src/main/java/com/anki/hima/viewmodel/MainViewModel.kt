package com.anki.hima.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anki.hima.utils.MsgListManager
import com.anki.hima.utils.Repository
import com.anki.hima.utils.bean.*
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.toastShort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MutableCollectionMutableState")
class MainViewModel(
    private val repo: Repository = Repository,
) : ViewModel() {

    var group by mutableStateOf(Group("", 0, 0, ""))
        private set
    var friends by mutableStateOf(FriendData(0, ""))
        private set
    private val _groupList = MutableStateFlow(ResInfo<List<Group>>())
    val groupList = _groupList.asStateFlow()

    //所有的消息
    //需要进行过滤才能用作最近消息
    private val _chatRoomMsgList = MutableStateFlow(mutableListOf<MsgDataBase>())
    val chatRoomMsgList = _chatRoomMsgList.asStateFlow()

    private val _simpleMsgList = MutableStateFlow(mutableListOf<MsgDataBase>())
    val simpleMsgList = _simpleMsgList.asStateFlow()

    private val _msgData = MutableStateFlow(MsgDataBase(null, "", 0, 0, "", 0, "", "", ""))
    val msgData = _msgData.asStateFlow()


    //初始化操作


    //连接到聊天室并且持续收取消息
    private fun initWebSocket() {
        viewModelScope.launch {
            if (_userId.value != 0) {
                repo.initWsSession()

                repo.receive().collect {
                    _chatRoomMsgList.value.add(it)

                    _msgData.value = it

                }
            }

        }
    }


    fun loadChatRoomList() {//加载聊天室消息，怎么判断是私聊还是群聊？
        viewModelScope.launch {
            //群聊
            when {
                group.id != 0 -> MsgListManager.getChatRoomMsgList(group.id).collect {
                    _chatRoomMsgList.value = it.toMutableList()
                }

                friends.friendId != 0 -> MsgListManager.getFriendMsgList(friends).collect {
                    _chatRoomMsgList.value = it.toMutableList()
                }

                else -> {}
            }

        }
    }

    fun getAllMsg() {
        viewModelScope.launch {
            MsgListManager.getAllMsg().collect {
                _simpleMsgList.value = it.toMutableList()
            }

        }
    }

    /////////////////////////////////////用户发送消息//////////////////////////////////
    fun sendMsg(msg: String) {
        viewModelScope.launch {
            val msgData = when {
                friends.friendId != 0 -> MsgData(
                    _name.value,
                    msg,
                    _userId.value,
                    null,
                    null,
                    friends.friendId,
                    friends.friendName
                )

                group.id != 0 -> MsgData(
                    _name.value,
                    msg,
                    _userId.value,
                    groupId = group.id,
                    groupName = group.groupName,
                    null,
                    null
                )

                else -> MsgData(_name.value, msg, _userId.value, null, null, null, null)
            }
            repo.sendMsg(msgData)
        }
    }


    fun resetData() {
        group = group.copy(id = 0)

        friends = friends.copy(friendId = 0)
    }

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _userId = MutableStateFlow(0)
    val userId = _userId.asStateFlow()


    fun changeGroup(id: Int, groupName: String) {
        group = group.copy(id = id, groupName = groupName)
    }

    fun changeFriendId(friendId: FriendData) {
        this.friends = friendId
    }
    ///////////////////////////////////////用户注册登录，用户自动登录//////////////////////////////

    private val _sign = MutableStateFlow(false)
    val sign = _sign.asStateFlow()
    private val _login = MutableStateFlow(false)
    val login = _login.asStateFlow()
    private fun autoLogin() {
        viewModelScope.launch {
            repo.autoLogin().collect {
                _login.value = it.code == 0
                _name.value = it.info?.userName ?: ""
                _userId.value = it.info?.id ?: 0
            }
        }
    }

    fun login(id: Int, pwd: String) {
        viewModelScope.launch {
            repo.login(id = id, pwd).collect {
                _login.value = it.code == 0
                _name.value = it.info?.userName ?: ""
                _userId.value = it.info?.id ?: 0
                if (it.code == 0) {
                    initWebSocket()
                }
            }
        }
    }

    fun signIn(uName: String, pwd: String) {
        viewModelScope.launch {
            repo.signIn(uName, pwd).collect {
                if (it.code == 0) {
                    _sign.value = true
                    _userId.value = it.info ?: 0
                    withContext(Dispatchers.Main) {
                        it.msg?.toastShort()
                    }
                }
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
    fun getGroupList() = viewModelScope.launch {
        if (_userId.value != 0) {
            repo.getGroupList(_userId.value).collect {
                _groupList.value = it
            }
        }

    }

    ///////////////////////////////新建群//////////////////////////////
    private val _addState = MutableStateFlow(ResInfo<Any?>())
    val addState = _addState.asStateFlow()

    //新建群组
    fun addGroup(groupName: String) = viewModelScope.launch {
        if (userId.value == 0) {
            return@launch
        }
        repo.addGroup(groupName, _userId.value).collect {
            _addState.value = it
        }
    }

    ///////////////////////////////////////////////////////怎么解决用户离线后消息缓存？//////////////////////////////////////////

    ///////////////////////////////////////////////////////获取好友列表//////////////////////////////////////////////////////
    private val _friendsList = MutableStateFlow(ResInfo<List<FriendData>>())
    val friendList = _friendsList.asStateFlow()

    //获取好友列表
    fun queryFriendList() {
        viewModelScope.launch {
            if (userId.value != 0) {
                repo.queryFriendList(userId.value).collect {
                    _friendsList.value = it
                }
            }
        }
    }

    private val _verifyList = MutableStateFlow(ResInfo<List<FriendApply>>())
    val verifyList = _verifyList.asStateFlow()
    fun queryApplyList(userId: Int) {
        viewModelScope.launch {
            repo.queryApplyList(userId).collect {
                _verifyList.value = it
            }
        }
    }


    //发送添加好友请求
    fun sendAddFriendMsg(from: Int, to: Int, verifyMsg: String) {
        viewModelScope.launch {
            repo.sendApply(from, to, verifyMsg).collect()
        }
    }

    private val _respVerifyMsg = MutableStateFlow(ResInfo<Any?>())
    val respVerifyResp = _respVerifyMsg.asStateFlow()

    //回应验证消息
    fun opApply(friendId: Int, op: Int) {
        viewModelScope.launch {
            repo.opApply(userId = userId.value, friendId = friendId, op = op).collect {
                _respVerifyMsg.value = it
            }
        }
    }

    //模糊查询用户
    private val _respSearchList = MutableStateFlow(ResInfo<List<User>>())
    val respSearchList = _respSearchList.asStateFlow()
    fun querySearchUserList(uid: Int? = null, name: String? = null) {
        viewModelScope.launch {
            repo.searchFriend(uid, name).collect {
                _respSearchList.value = it
            }
        }
    }

    init {
        autoLogin() //检查用户状态
        initWebSocket()
        getAllMsg()
    }
}