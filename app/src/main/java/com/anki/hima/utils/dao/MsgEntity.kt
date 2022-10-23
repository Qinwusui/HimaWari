package com.anki.hima.utils.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MsgList")
data class MsgDataBase(
    @PrimaryKey(autoGenerate = true) val msgIndex: Int?,
    @ColumnInfo(name = "nickName") val nickName: String,
    @ColumnInfo(name = "qq") val qq: String,
    @ColumnInfo(name = "chatRoomId") val chatRoomId: String,
    @ColumnInfo(name = "msg") val msg: String,
    @ColumnInfo(name = "timeStamp") val time: String,
)
@Entity(tableName = "simpleMsg")
data class SimpleMsg(
    @PrimaryKey(autoGenerate = true) val msgIndex: Int?,
    @ColumnInfo(name = "nickName") val nickName: String,
    @ColumnInfo(name = "msg") val time: String,
    @ColumnInfo(name = "chatRoomId") val chatRoomId: String
)