package com.anki.hima.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anki.hima.utils.dao.MsgDataBase
import com.anki.hima.utils.dao.MsgListDao

@Database(entities = [MsgDataBase::class], version = 1)
abstract class MsgManager : RoomDatabase() {
    abstract fun msgListDao(): MsgListDao
}
