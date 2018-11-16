package com.hipark.randomer.data.Source

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.hipark.randomer.data.Item

@Database(entities = arrayOf(Item::class), version = 1)
abstract class RandomerDatabase : RoomDatabase() {

    abstract fun itemDao() : ItemsDao

    companion object {

        private var INSTANCE: RandomerDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context) : RandomerDatabase {
            synchronized(lock) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        RandomerDatabase::class.java, "Items.db").build()
                }
                return INSTANCE!!
            }
        }
    }
}
