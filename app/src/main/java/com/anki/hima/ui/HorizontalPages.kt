package com.anki.hima.ui

open class HorizontalPages(val name: String, val title: String) {
    //消息页
    object MessageHorizontalPages : HorizontalPages("MessageScreen", "消息")

    //联系人
    object ContactHorizontalPages : HorizontalPages("ContactScreen", "联系人")

    //群组
    object GroupHorizontalPages : HorizontalPages("GroupScreen", "群组")

    //关于
    object MeHorizontalPages : HorizontalPages("MeScreen", "我")

}
