package com.anki.hima.utils.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MsgList")
data class MsgDataBase(
    @PrimaryKey(autoGenerate = true) val msgIndex: Int?,
    @ColumnInfo(name = "nickName") val userName: String,
    @ColumnInfo(name = "from") val from: Int,
    @ColumnInfo(name = "chatRoomId") val groupId: Int?,
    @ColumnInfo(name = "chatRoomName") val groupName:String?,
    @ColumnInfo(name = "to") val to: Int?,
    @ColumnInfo(name = "toNickName") val toUserName: String?,
    @ColumnInfo(name = "msg") val msg: String,
    @ColumnInfo(name = "timeStamp") val time: String,
)
