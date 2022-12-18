package com.anki.hima.utils.bean


/**
 * 发送给服务器的数据对象
 */

data class MsgData(
    val nickName: String,
    val msg: String,
    val chatRoomId: String,
    val qq: String
)


data class SignInfo(
    val content: String = ""
)

data class FriendItem(
    val qq: String,
    val uName: String
)

data class ResFriendList(
    val code: Int = -1,
    val success: Boolean = false,
    val list: MutableList<FriendItem> = mutableListOf()
)

data class VerifyInfo(
    val from: String,
    val to: String,
    val verifyMsg: String,
    val submitTime: String
)

data class VerifyResp(
    val verifyInfo: VerifyInfo,
    val admit: Boolean
)

data class VerifyList(
    val code: Int = -1,
    val success: Boolean = false,
    val list: MutableList<VerifyInfo> = mutableListOf()
)

/**
 *  返回
 *
 *      0：注册/登录成功
 *      1：注册/登录失败
 */

data class ResInfo(
    val code: Int = -1,
    val success: Boolean = false,
    val msg: String = ""
)

data class GroupList(
    val list: MutableList<Group>
)

data class Group(
    val groupName: String,
    val groupId: String
)

