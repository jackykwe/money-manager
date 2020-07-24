package com.kaeonx.moneymanager.xerepository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseXERow::class], version = 1)
abstract class XEDatabase : RoomDatabase() {
    // You can have multiple tables and multiple daos
    abstract val databaseXEDao: DatabaseXEDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: XEDatabase

        fun getInstance(context: Context): XEDatabase {
            synchronized(XEDatabase::class.java) {
                if (!Companion::INSTANCE.isInitialized) {
                    // Opening a connection to a database is expensive!
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        XEDatabase::class.java,
                        "xe_database"
                    ).build()
                }
                return INSTANCE
            }
        }
    }
}
