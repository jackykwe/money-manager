package com.kaeonx.moneymanager.txnrepository.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val TAG = "dtb"

@Database(entities = [DatabaseTxn::class], version = 1)
abstract class TxnDatabase : RoomDatabase() {
    abstract val txnDatabaseDao: TxnDatabaseDao

    companion object {

        @Volatile
//        private lateinit var INSTANCE: TxnDatabase
        private var INSTANCE: TxnDatabase? = null

        fun getInstance(context: Context, userID: String): TxnDatabase {
            Log.d(TAG, "getInstance: called")
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    Log.d(TAG, "WARN: OPENING INSTANCE TO DATABASE")
                    // Opening a connection to a database is expensive!
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TxnDatabase::class.java,
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
