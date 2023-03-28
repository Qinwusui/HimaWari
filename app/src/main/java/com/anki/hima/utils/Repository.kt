package com.anki.hima.utils

import android.content.Context
import androidx.core.content.edit
import com.anki.hima.app.HimaApplication
import com.anki.hima.utils.bean.*
import com.anki.hima.utils.dao.MsgDataBase
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.*
import io.ktor.util.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

object Repository {
    /**
     * 承载业务
     *
     *         - 1.用户注册登录
     *         - 2.初始化webSocket Session
     *         - 3.保存消息到数据库
     *         - 4.从消息数据库获取消息
     */
    private const val BASE_URL = "http://172.16.15.123:54443" //更换为自己的后端服务器IP
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson { }
        }
        install(HttpCookies)
    }
    private val wsClient: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(WebSockets) {
                contentConverter = GsonWebsocketContentConverter()
            }
            install(Auth) {
                basic { }
            }
        }
    }
    private val json = GsonBuilder().create()

    private var wsSession: WebSocketSession? = null


    /**
     * 建立客户端与服务器连接
     */
    suspend fun initWsSession() {
        if (wsSession == null) {
            wsSession = try {
                wsClient.webSocketSession(
                    host = "172.16.15.123", //更换为自己的后端服务器IP
                    port = 54443,
                    method = HttpMethod.Get,
                    path = "/anki/chat",
                ) {
                    basicAuth("wusui", "Qinsansui233...")
                }
            } catch (e: Exception) {
                null
            }
        }
    }


    fun getGroupList(userId: Int) = flowByIO {
        val body = client.post("$BASE_URL/group/list") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(
                User(
                    id = userId,
                    pwd = null,
                    userName = null
                )
            )
        }
        body.body<ResInfo<List<Group>>>()
    }

    /**
     * 发送群聊消息
     */
    suspend fun sendMsg(msgData: MsgData) {
        wsSession?.sendSerializedBase(
            msgData, KotlinxWebsocketSerializationConverter(Json), Charsets.UTF_8
        )
    }

    /**
     * 开始聊天
     * 该方法应该一直保持运行
     */
    suspend fun receive() = channelFlow {

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val time = simpleDateFormat.format(Date())
        wsSession?.incoming?.consumeEach {//收到消息，消息内容包括：昵称、消息、所在聊天室、QQ
            val msgData = Json.decodeFromString<MsgData>(it.readBytes().decodeToString())
            val msgDataBase = if (msgData.groupId == null) {
                MsgDataBase(
                    null,
                    msgData.nickName,
                    msgData.qq,
                    null,
                    null,
                    msgData.to,
                    msgData.toNickName,
                    msgData.msg,
                    time
                )
            } else {
                MsgDataBase(
                    null,
                    msgData.nickName,
                    msgData.qq,
                    msgData.groupId,
                    msgData.groupName,
                    null,
                    null,
                    msgData.msg,
                    time
                )
            }
            //插入到数据库
            MsgListManager.insertMsgToDb(msgDataBase).collect()
            //将消息发送到UI
            send(msgDataBase)
        }

    }

    private fun saveUser(content: String) {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        sp.edit(true) {
            putString("info", content)
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /////////用户注册登录相关////////////////////////////////////////////////////////////////////////////
    /**
     * @need 注册，需要用户名，密码
     * @return [ResInfo]
     */
    fun signIn(userName: String, pwd: String) = flowByIO {
        val encPwd = pwd.encodeBase64()
        val req = client.post("$BASE_URL/user/add") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(
                User(
                    id = null,
                    userName = userName,
                    pwd = encPwd
                )
            )
        }
        val b = req.body<ResInfo<Int>>()
        b
    }

    /**
     * 登录逻辑，需要QQ号和密码
     */
    fun login(id: Int, pwd: String) = flowByIO {
        val encPwd = pwd.encodeBase64()
        val user = User(
            userName = null,
            id = id,
            pwd = encPwd
        )
        val req = client.post("$BASE_URL/user/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                user
            )
        }
        val b = req.body<ResInfo<Any?>>()
        b.toString().loge()
        if (b.code == 0) {
            val content = json.toJson(user).encodeBase64()
            saveUser(content)
        }
        b
    }

    private fun getLocalContent(): String {
        val context = HimaApplication.context
        //从SharedPref中读取加密的用户信息
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sp.getString("info", "") ?: ""
    }

    //自动登录
    fun autoLogin() = flowByIO {
        val content = getLocalContent().decodeBase64String()
        content.loge()
        if (content == "") return@flowByIO false
        val user = json.fromJson(content, User::class.java)
        val req = client.post("$BASE_URL/user/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                user
            )
        }
        try {
            val b = req.body<ResInfo<Any?>>()
            b.toString().loge()
            if (b.code == 0) {
                saveUser(content.encodeBase64())
            }
            b.code == 0
        } catch (e: Exception) {
            false
        }

    }


    /**
     * 登出
     */
    suspend fun logOut() {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        sp.edit(true) {
            remove("info")
        }
        wsSession?.close()
        wsSession = null
    }
    //////////////////////////////////////////////////联系人/////////////////////////////////////
    /**
     * 获取好友列表
     */
    fun queryFriendList(userId: Int) = flowByIO {
        val list = client.post("$BASE_URL/friends/list") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(
                User(
                    id = userId,
                    userName = null,
                    pwd = null
                )
            )
        }
        list.body<ResInfo<List<FriendData>>>()

    }

    //好友模糊查找
    fun searchFriend(friendId: Int) = flowByIO {
        val res = client.post("$BASE_URL/friends/search") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(
                FriendData(
                    friendId = friendId,
                    friendName = null,
                )
            )
        }
        val b = res.body<ResInfo<List<User>>>()
        b.toString().loge()
        b
    }

    //添加联系人功能
    fun sendApply(userId: Int, to: Int, msg: String) = flowByIO {
        //todo 需要检查verifyMsg中是否包含违禁词

        val res = client.post("$BASE_URL/friends/add") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(
                FriendApply(
                    from = userId,
                    to = to,
                    msg = msg,
                    createdTime = null
                )
            )
        }

        val body = res.body<ResInfo<Any?>>()

        body
    }

    //回应验证请求()
    fun opApply(userId: Int, friendId: Int, op: Int) = flowByIO {
        val res = client.post("$BASE_URL/friends/op") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                ApplyOp(
                    from = userId,
                    to = friendId,
                    op = op
                )
            )
        }
        res.body<ResInfo<Any?>>()
    }

    //获取验证消息
    fun queryApplyList(userId: Int) = flowByIO {
        val res = client.post("$BASE_URL/friends/list") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                User(
                    userName = null,
                    id = userId,
                    pwd = null
                )
            )
        }
        res.body<ResInfo<List<FriendApply>>>()
    }

    //新建群组
    fun addGroup(groupName: String, userId: Int) = flowByIO {

        val res = client.post("$BASE_URL/group/add") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                GroupAddData(
                    groupName = groupName,
                    user = User(
                        id = userId,
                        pwd = null,
                        userName = null
                    )
                )
            )
        }
        res.body<ResInfo<Any?>>()
    }

    //删除群组
//    fun delGroup(groupId: Int, ownerQQ: String) = flowByIO {
//        val res = client.post("$BASE_URL/anki/group/delete") {
//            headers {
//                append(HttpHeaders.ContentType, "application/json")
//            }
//            setBody(
//                GroupDeleteData(
//                    groupId, ownerQQ, "Qinsansui233...".encodeBase64()
//                )
//            )
//        }
//        res.body<ResInfo<>>()
//    }

    fun joinGroup(groupId: Int, userId: Int, subscription: String) = flowByIO {
        val res = client.post("$BASE_URL/group/join") {
            header(HttpHeaders.ContentType, "application/json")
            setBody(
                JoinGroupData(
                    userId = userId,
                    groupId = groupId,
                    sub = subscription
                )
            )
        }
        res.body<ResInfo<Any?>>()
    }
}