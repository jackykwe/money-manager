package com.kaeonx.moneymanager.userrepository.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val TAG = "dtb"

@Database(entities = [DatabaseTxn::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDatabaseDao: UserDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context, userID: String): UserDatabase {
            Log.d(TAG, "getInstance: called")
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    Log.d(TAG, "WARN: OPENING INSTANCE TO DATABASE")
                    // Opening a connection to a database is expensive!
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "txn_database_$userID"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // Used when logging out
        fun dropInstance() {
            INSTANCE = null
        }

    }
}
