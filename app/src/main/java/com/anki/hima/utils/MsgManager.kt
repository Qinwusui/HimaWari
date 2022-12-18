package com.anki.hima.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.dao.MsgListDao
import com.anki.hima.utils.dao.SimpleListDao
import com.anki.hima.utils.dao.SimpleMsg

@Database(entities = [MsgDataBase::class], version = 1)
abstract class MsgManager : RoomDatabase() {
    abstract fun msgListDao(): MsgListDao
}

@Database(entities = [SimpleMsg::class], version = 1)
abstract class SimpleMsgManager : RoomDatabase() {
    abstract fun simpleListDao(): SimpleListDao
}