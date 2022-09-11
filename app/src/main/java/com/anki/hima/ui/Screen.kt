package com.anki.hima.ui

open class Screen(val name: String, val title: String) {
    //消息页
    object MessageScreen : Screen("MessageScreen", "消息")

    //联系人
    object ContactScreen : Screen("ContactScreen", "联系人")

    //群组
    object GroupScreen : Screen("GroupScreen", "群组")

    //关于
    object AboutScreen : Screen("AboutScreen", "我")

    object ChatScreen : Screen("ChatScreen", "聊天")

}
