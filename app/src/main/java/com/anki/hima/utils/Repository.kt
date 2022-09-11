package com.anki.hima.utils

import android.content.Context
import com.anki.hima.app.HimaApplication
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.coroutines.CoroutineContext

object Repository {
    private const val BASE_URL = "http://v2.liusui.xyz:54321"
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
        install(HttpCookies)
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
     *          时间戳:  timeStamp
     */
    fun signIn(userName: String, pwd: String) = flowByIO {

        val post = client.post("$BASE_URL/user/sign") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                SignInfo(
                    content = "$userName||$pwd||$${Date().time}".encodeBase64()
                )
            )
        }
        val resInfo = post.body<ResInfo>()
        resInfo.code == 0 && resInfo.success
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
            resInfo.code == 0 && resInfo.success
        } else {
            false
        }

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