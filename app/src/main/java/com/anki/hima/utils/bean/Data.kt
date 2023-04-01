package com.anki.hima.utils.bean

import kotlinx.serialization.Serializable
import androidx.annotation.Keep

import kotlinx.serialization.SerialName



/**
 * 发送给服务器的数据对象
 */

@Serializable
data class MsgData(
    val nickName: String,//昵称
    val msg: String, //消息
    val qq: Int,
    val groupId: Int?, //聊天室id
    val groupName: String?,
    val to: Int?,
    val toNickName: String?
)

@Serializable
data class User(
    val id: Int?,
    val userName: String?,
    val pwd: String?
)


@Serializable
data class FriendApply(
    val from: Int,
    val to: Int,
    val msg: String,
)

@Serializable
data class ApplyOp(
    val from: Int,
    val to: Int,
    val op: Int,
)


@Serializable
data class ResInfo<T>(
    val code: Int = -1,
    val info: T? = null,
    val msg: String? = ""
)

@Serializable
data class Group(
    val groupName: String,
    val id: Int,
    val ownerId: Int,
    val createdTime: String,
)

@Serializable
data class GroupAddData(
    val groupName: String,
    val user: User
)

@Serializable
data class GroupDeleteData(
    val groupId: Int,
    val ownerQQ: String,
    val token: String
)

@Serializable
data class JoinGroupData(
    val userId: Int,
    val groupId: Int,
    val sub: String,
)

@Serializable
data class FriendData(
    val friendId: Int,
    val friendName: String?,
)