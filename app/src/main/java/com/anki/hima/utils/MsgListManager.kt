package com.anki.hima.utils

import androidx.room.Room
import com.anki.hima.app.HimaApplication
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.dao.MsgListDao
import com.anki.hima.utils.dao.SimpleListDao
import com.anki.hima.utils.dao.SimpleMsg

/**
 * 消息列表，简略，非某一个聊天室
 */
object MsgListManager {
    private var msgListDao: MsgListDao
    private var simpleMsgListDao: SimpleListDao

    init {
        val db = Room.databaseBuilder(
            HimaApplication.context, MsgManager::class.java, "MsgList"
        ).build()
        val simpleMsgDb = Room.databaseBuilder(
            HimaApplication.context, SimpleMsgManager::class.java, "simpleMsgList"
        ).build()
        msgListDao = db.msgListDao()
        simpleMsgListDao = simpleMsgDb.simpleListDao()
    }


    /**
     * 1.收到消息时，将消息插入到消息数据库
     */
    fun insertMsgToDb(msgDataBase: MsgDataBase) = flowByIO {
        try {
            msgListDao.insertMsg(msgDataBase)
        } catch (e: Exception) {
            e.message?.loge()
        }
    }

    fun insertSimpleMsgToDb(simpleMsg: SimpleMsg) = flowByIO {
        try {
            simpleMsgListDao.insertSimpleMsg(simpleMsg)
        } catch (e: Exception) {
            e.message?.loge()
        }
    }

    /**
     * 2.获取简略消息列表
     */
    fun getSimpleMsgList() = flowByIO {
        simpleMsgListDao.getAll().distinctBy { it.chatRoomId }
    }

    /**
     * 3.获取某个聊天室的消息列表
     */
    fun getMsgList(chatRoomId: String) = flowByIO {
        msgListDao.getChatRoomMsg(chatRoomId)
    }
}
