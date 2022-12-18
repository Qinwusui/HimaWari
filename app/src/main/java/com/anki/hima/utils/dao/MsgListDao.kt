package com.anki.hima.utils.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MsgListDao {
    //    @Query("select * from MsgList")
//    fun getAll(): List<MsgDataBase>
    @Insert
    fun insertMsg(msgDataBase: MsgDataBase)

    //    @Delete
//    fun deleteMsg(msgDataBase: MsgDataBase)
//    @Update
//    fun updateMsg(msgDataBase: MsgDataBase)
    @Query("select * from MsgList where chatRoomId=:chatRoomId")
    fun getChatRoomMsg(chatRoomId: String): List<MsgDataBase>
}

@Dao
interface SimpleListDao {
    @Query("select * from simpleMsg")
    fun getAll(): List<SimpleMsg>

    @Insert
    fun insertSimpleMsg(msg: SimpleMsg)
//    @Delete
//    fun deleteSimpleMsg(msg: SimpleMsg)
//    @Update
//    fun updateSimpleMsg(msg: SimpleMsg)
}