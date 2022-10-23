package com.anki.hima.utils

import android.content.Context
import androidx.annotation.Keep
import androidx.core.content.edit
import com.anki.hima.app.HimaApplication
import com.anki.hima.utils.bean.GroupList
import com.anki.hima.utils.bean.MsgData
import com.anki.hima.utils.bean.ResInfo
import com.anki.hima.utils.bean.SignInfo
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.dao.SimpleMsg
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
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
import io.ktor.util.decodeBase64String
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.serialization.sendSerializedBase
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
    private const val BASE_URL = "http://:54322" //更换为自己的后端服务器IP
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson {  }
        }
        install(HttpCookies)
    }
    private val wsClient: HttpClient by lazy {
        HttpClient(CIO) {
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
        try {
            _wsSession = wsClient.webSocketSession(
                host = "", //更换为自己的后端服务器IP
                port = 54322,
                method = HttpMethod.Get,
                path = "/anki/chat"
            )
        } catch (e: Exception) {
            e.message?.loge()
            "没有连接到聊天服务器，5秒后重试...".toastShort()
            withTimeout(5000) {
                initWsSession()
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
        val content = userName.encodeToContent(qq, pwd) //加密后的用户信息
        val req = client.post("$BASE_URL/user/sign") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content = content))
        }
        val b = req.body<ResInfo>()
        b.success
    }

    /**
     * 登录逻辑，需要QQ号和密码
     */
    fun login(userName: String, qq: String, pwd: String) = flowByIO {
        val content = userName.encodeToContent(qq, pwd) //加密后的用户信息
        val req = client.post("$BASE_URL/user/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content = content))
        }
        val b = req.body<ResInfo>()
        if (b.success) {
            saveUser(content)
        }
        b.success
    }

    //自动登录
    fun autoLogin() = flowByIO {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val content = sp.getString("info", "")!!
        val req = client.post("$BASE_URL/user/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content = content))
        }
        try {
            val b = req.body<ResInfo>()
            if (b.success) {
                saveUser(content)
            }
            b.success
        } catch (e: Exception) {
            e.message?.loge()
            false
        }

    }

    /**
     * 获取相关信息
     */
    fun getInfo(index: Int): String {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val strList = sp.getString("info", "")!!
        val str = strList.decodeBase64String().split("||")
        if (str.size != 3) {
            return "Guest"
        }
        if (index >= str.size) {
            return "Guest"
        }
        return str[index]
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
}