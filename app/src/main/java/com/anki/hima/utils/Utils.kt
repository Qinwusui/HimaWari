package com.anki.hima.utils

import android.util.Log
import android.widget.Toast
import com.anki.hima.app.HimaApplication
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

fun String.toastShort() =
    Toast.makeText(HimaApplication.context, this, Toast.LENGTH_SHORT).show()

fun String.loge() = Log.e("TAG", this)

fun <T> flowByIO(
    coroutineContext: CoroutineContext = Dispatchers.IO, content: suspend () -> T
) = flow {
    emit(content())
}.distinctUntilChanged().flowOn(coroutineContext)

fun String.encodeToSign(qq: String, pwd: String) = "$this||$qq||$pwd".encodeBase64()

fun String.encodeToLog(pwd: String) = "$this||$pwd".encodeBase64()


enum class TypeByUser {
    UserName, UserId, Pwd
}
