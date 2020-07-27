package com.kaeonx.moneymanager.xerepository.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kaeonx.moneymanager.activities.App

@Database(entities = [DatabaseXERow::class], version = 1)
abstract class XEDatabase : RoomDatabase() {
    // You can have multiple tables and multiple daos
    abstract val xeDatabaseDao: XEDatabaseDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: XEDatabase

        fun getInstance(): XEDatabase {
            synchronized(this) {
                if (!Companion::INSTANCE.isInitialized) {
                    // Opening a connection to a database is expensive!
                    INSTANCE = Room.databaseBuilder(
                        App.context,
                        XEDatabase::class.java,
                        "xe_database"
                    ).build()
                }
                return INSTANCE
            }
        }
    }
}
