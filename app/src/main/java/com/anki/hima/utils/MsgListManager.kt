package com.anki.hima.utils

import androidx.room.Room
import com.anki.hima.app.HimaApplication
import com.anki.hima.utils.bean.FriendData
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.dao.MsgListDao

/**
 * 消息列表，简略，非某一个聊天室
 */
object MsgListManager {
    private var msgListDao: MsgListDao

    init {
        val db = Room.databaseBuilder(
            HimaApplication.context, MsgManager::class.java, "MsgList"
        ).build()
        msgListDao = db.msgListDao()
    }


    /**
     * 收到消息时，将消息插入到消息数据库
     */
    fun insertMsgToDb(msgDataBase: MsgDataBase) = flowByIO {
        try {
            msgListDao.insertMsg(msgDataBase)
        } catch (e: Exception) {
            e.message?.loge()
        }
    }


    /**
     * 获取群聊的消息
     */
    fun getChatRoomMsgList(chatRoomId: Int) = flowByIO {
        msgListDao.getChatRoomMsg(chatRoomId)
    }

    /**
     * 获取某个好友发送的消息
     */
    fun getFriendMsgList(to: FriendData) = flowByIO {
        msgListDao.getFriendsMsg(to.friendId)
    }

    /**
     * 获取消息列表
     */
    fun getAllMsg() = flowByIO {
        msgListDao.getAllMsg()
    }
}
