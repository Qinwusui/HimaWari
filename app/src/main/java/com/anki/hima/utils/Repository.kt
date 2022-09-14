package com.anki.hima.utils

import android.content.Context
import androidx.core.content.edit
import com.anki.hima.app.HimaApplication
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

object Repository {
    private const val BASE_URL = "http://192.168.123.34:54321"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
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
    fun disConnect() {
        _wsSession = null
    }

    /**
     * 发送给服务器的数据对象
     */
    data class IncomingData(
        val nickName: String,
        val msg: String,
        val chatRoomId: String
    )

    data class OutGoingData(
        val nickName: String,
        val msg: String
    )

    /**
     * 建立客户端与服务器连接
     */
    suspend fun initWsSession() {
        try {
            _wsSession = wsClient.webSocketSession(
                host = "192.168.123.34",
                port = 54321,
                method = HttpMethod.Get,
                path = "/anki/chat?nickName=${getUserInfo(false)}&chatRoomId=0"
            )
        } catch (e: Exception) {
            "没有连接到聊天服务器，5秒后重试...".toastShort()
            withTimeout(5000) {
                initWsSession()
            }
        }
    }

    /**
     * 开始聊天
     */
    suspend fun receive() = channelFlow {
        _wsSession?.incoming?.consumeEach {
            val gson = Gson()
            val b = gson.fromJson(it.readBytes().decodeToString(), OutGoingData::class.java)
            send(b)
        }
    }

    /**
     * 发送消息
     */
    suspend fun sendMsg(msg: String) {
        _wsSession?.sendSerializedBase(
            IncomingData(getUserInfo(false), msg, "0"), GsonWebsocketContentConverter(),
            Charsets.UTF_8
        )

    }


    fun getUserInfo(needQQ: Boolean): String {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val content = sp.getString("info", "")
        val decodeStr = content!!.decodeBase64String().split("||")
        return if (decodeStr.size != 3) {
            "Visitor"
        } else {
            if (needQQ) {
                decodeStr[1]
            } else {
                decodeStr[0]
            }

        }
    }


    /**
     * 首次启动检查是否存在登录信息
     *
     * 存在则尝试登录
     *
     *          登录成功则进入主页面
     *          登录失败则进入登录/注册页面
     *
     * 不存在则进入登录/注册页面
     */
    private fun checkUserLogin(): Boolean {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val token = sp.getString("info", "")
        return try {
            val decodeStr = token!!.decodeBase64String().split("||")
            decodeStr.size == 3
        } catch (e: Exception) {
            false
        }
    }

    /**
     *  用户注册相关
     *
     *      Need:
     *          用户名:  userName
     *          密码:    Pwd
     */
    fun signIn(userName: String, qq: String, pwd: String) = flowByIO {
        val content = "$userName||$qq||$pwd".encodeBase64()
        val post = client.post("$BASE_URL/user/sign") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                SignInfo(
                    content = content
                )
            )
        }
        val resInfo = post.body<ResInfo>()
        if (resInfo.code == 0 && resInfo.success) {

            saveUser(content)
        }
        resInfo.code == 0 && resInfo.success
    }

    private fun saveUser(content: String) {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        sp.edit(true) {
            putString("info", content)
        }
    }

    fun getQQ(content: String) = flowByIO {
        val strList = content.decodeBase64String().split("||")
        val loginUser = LoginUser(
            uName = strList[0],
            pwd = strList[1]
        )
        val res = client.post("$BASE_URL/user/requestQQ") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(loginUser)
        }
        val qq = res.body<SignInfo>()
        qq
    }

    /**
     * 用户登录相关
     *
     *      Need:
     *          密文
     */
    fun loginBySp() = flowByIO {
        val context = HimaApplication.context
        val sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val content = sp.getString("info", "")
        val pass = checkUserLogin()
        if (pass) {
            val post = client.post("$BASE_URL/user/login") {
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(
                    SignInfo(
                        content = content!!
                    )
                )
            }
            val resInfo = post.body<ResInfo>()
            val b = resInfo.code == 0 && resInfo.success
            if (b) {
                saveUser(content!!)
            }
            b
        } else {
            false
        }

    }

    /**
     * 使用用户名与密码进行登录操作
     */
    fun login(userName: String, pwd: String) = flowByIO {
        val content = "$userName||$pwd".encodeBase64()
        val post = client.post("$BASE_URL/user/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(SignInfo(content = content))
        }
        val resInfo = post.body<ResInfo>()
        val b = resInfo.code == 0 && resInfo.success
        if (b) {
            saveUser(content)
        }
        b
    }

    /**
     * 注册请求体
     *
     *      用户名||密码||时间
     */
    @Serializable
    data class SignInfo(
        val content: String
    )

    @Serializable
    data class LoginUser(
        val uName: String,
        val pwd: String
    )

    /**
     *  返回
     *
     *      0：注册/登录成功
     *      1：注册/登录失败
     */
    @Serializable
    data class ResInfo(
        val code: Int,
        val success: Boolean,
        val msg: String
    )

    @Serializable
    data class MessageScreenData(
        val name: String,
        val iconUrl: String,
        val simpleText: String
    )


    /**
     *      泛型方法，用于自定义协程所在线程
     */
    private fun <T> flowByIO(
        coroutineContext: CoroutineContext = Dispatchers.IO, content: suspend () -> T
    ) = flow {
        emit(content())
    }.distinctUntilChanged().flowOn(coroutineContext)


}