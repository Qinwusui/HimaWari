package com.anki.hima.utils.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MsgListDao {

    @Insert
    fun insertMsg(msgDataBase: MsgDataBase)


    @Query("select * from MsgList where chatRoomId=:groupId")
    fun getChatRoomMsg(groupId: Int): List<MsgDataBase>

    @Query("select * from MsgList where `to`=:to")
    fun getFriendsMsg(to: Int): List<MsgDataBase>

    @Query(
        """
        select msgIndex,nickName,`from`,chatRoomId,chatRoomName,`to`,toNickName,msg,timeStamp from
(select *,count(distinct chatRoomId) from MsgList group by chatRoomId)
 order by msgIndex desc
    """
    )
    fun getAllMsg(): List<MsgDataBase>

}
