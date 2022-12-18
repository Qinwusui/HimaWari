package com.anki.hima.utils

import android.content.Context
import androidx.core.content.edit
import com.anki.hima.app.HimaApplication
import com.anki.hima.utils.bean.GroupList
import com.anki.hima.utils.bean.MsgData
import com.anki.hima.utils.bean.ResFriendList
import com.anki.hima.utils.bean.ResInfo
import com.anki.hima.utils.bean.SignInfo
import com.anki.hima.utils.bean.VerifyInfo
import com.anki.hima.utils.bean.VerifyList
import com.anki.hima.utils.bean.VerifyResp
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.dao.SimpleMsg
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.gson.GsonWebsocketContentConverter
import io.ktor.serialization.gson.gson
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.serialization.sendSerializedBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Repository {
    /**
     * 承载业务
     *
     *         - 1.用户注册登录
     *         - 2.初始化webSocket Session
     *         - 3.保存消息到数据库
     *         - 4.从消息数据库获取消息
     */
    private const val BASE_URL = "http://192.168.1.32:54322" //更换为自己的后端服务器IP
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
        }
    }


    private var _wsSession: WebSocketSession? = null


    /**
     * 建立客户端与服务器连接
     */
    suspend fun initWsSession() {
        if (_wsSession == null) {
            _wsSession = try {
                wsClient.webSocketSession(
                    host = "192.168.1.32", //更换为自己的后端服务器IP
                    port = 54322,
                    method = HttpMethod.Get,
                    path = "/anki/chat",
                )
            } catch (e: Exception) {
                null
            }
        }
    }


    fun getGroupList() = flowByIO {
        val body = client.get("$BASE_URL/anki/groupList")
        body.body<GroupList>()
    }

    /**
     * 发送消息
     */
    suspend fun sendMsg(nickName: String, chatRoomId: String, qq: String, msg: String) {
        _wsSession?.sendSerializedBase(
            MsgData(nickName, msg, chatRoomId, qq),
            GsonWebsocketContentConverter(),
            Charsets.UTF_8
        )
    }

    /**
     * 开始聊天
     * 该方法应该一直保持运行
     */
    suspend fun receive() = channelFlow {
        val gson = Gson()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val time = simpleDateFormat.format(Date())
        _wsSession?.incoming?.consumeEach {//收到消息，消息内容包括：昵称、消息、所在聊天室、QQ
            val msgData = gson.fromJson(it.readBytes().decodeToString(), MsgData::class.java)
            //插入到数据库
            val msgDataBase =
                MsgDataBase(
                    null,
                    msgData.nickName,
                    msgData.qq,
                    msgData.chatRoomId,
                    msgData.msg,
                    time
                )
            MsgListManager.insertMsgToDb(msgDataBase).collect()
            val simpleMsg =
                SimpleMsg(
                    null,
                    msgData.nickName,
                    time,
                    msgData.chatRoomId
                )
            MsgListManager.insertSimpleMsgToDb(simpleMsg).collect()
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
     * 注册，需要用户名，QQ号，密码
     */
    fun signIn(userName: String, qq: String, pwd: String) = flowByIO {
        val content = userName.encodeToSign(qq, pwd) //加密后的用户信息
        val req = client.post("$BASE_URL/user/sign") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content))
        }
        val b = req.body<ResInfo>()
        b.success
    }

    /**
     * 登录逻辑，需要QQ号和密码
     */
    fun login(qq: String, pwd: String) = flowByIO {
        val content = qq.encodeToLog(pwd) //加密后的用户信息
        val req = client.post("$BASE_URL/user/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content))
        }
        val b = req.body<ResInfo>()
        if (b.success) {
            saveUser(content)
        }
        b.success
    }

    private fun getLocalContent(): String {
        val context = HimaApplication.context
        //从SharedPref中读取加密的用户信息
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sp.getString("info", "") ?: ""
    }

    //自动登录
    fun autoLogin() = flowByIO {
        val content = getLocalContent()
        val req = client.post("$BASE_URL/user/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content))
        }
        try {
            val b = req.body<ResInfo>()
            if (b.success) {
                saveUser(content)
            }
            b.success
        } catch (e: Exception) {
            false
        }

    }

    //获取QQ
    fun requestQQ() = flowByIO {
        val content = getLocalContent()
        val res = client.post("$BASE_URL/user/requestQQ") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content))
        }.body<ResInfo>()
        res
    }

    //获取用户名
    fun requestUname() = flowByIO {
        val content = getLocalContent()
        return@flowByIO client.post("$BASE_URL/user/uname") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content))
        }.body<ResInfo>()
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
        _wsSession?.close()
        _wsSession = null
    }
    //////////////////////////////////////////////////联系人/////////////////////////////////////
    /**
     * 首先是联系人查询功能
     */
    fun queryFriends(str: String) = flowByIO {
        val list = client.post("$BASE_URL/user/queryFriends") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(str)
        }
        list.body<ResFriendList>()

    }

    //获取联系人查询列表
    fun querySearchList(str: String) = flowByIO {
        val resp = client.post("$BASE_URL/user/querySearchFriends") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(str)
        }
        resp.body<ResFriendList>()
    }

    //添加联系人功能
    fun addFriend(ownerQQ: String, friendQQ: String, verifyMsg: String) = flowByIO {
        //todo 需要检查verifyMsg中是否包含违禁词

        val res = client.post("$BASE_URL/user/addVerifyMsg") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            val timeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())
            setBody(
                VerifyInfo(
                    from = ownerQQ,
                    to = friendQQ,
                    verifyMsg = verifyMsg,
                    submitTime = timeFormat
                )
            )
        }

        val body = res.body<ResInfo>()

        withContext(Dispatchers.Main) {
            if (body.success) {
                "发送成功，请等待对方回应~".toastShort()
            } else {
                "发送失败，之前是否发送过请求？".toastShort()
            }
        }
        body
    }

    //回应验证请求()
    fun respVerifyMsg(verifyInfo: VerifyInfo, admit: Boolean) = flowByIO {
        val res = client.post("$BASE_URL/user/respVerifyMsg") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(VerifyResp(verifyInfo, admit))
        }
        res.body<ResInfo>()
    }

    //获取验证消息
    fun queryVerifyMsg(qq: String) = flowByIO {
        val res = client.post("$BASE_URL/user/queryVerifyMsg") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(qq)
        }
        res.body<VerifyList>()
    }
}