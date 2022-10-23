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

/**
 * 注册请求体
 *
 *      用户名||密码||时间
 */
data class SignInfo(
    val content: String = ""
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