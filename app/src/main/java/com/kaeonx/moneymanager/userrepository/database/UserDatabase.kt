package com.kaeonx.moneymanager.userrepository.database

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId

private const val TAG = "dtb"

@Database(
    entities = [
        DatabaseAccount::class,
        DatabaseCategory::class,
        DatabaseTransaction::class,
        DatabasePreference::class
    ],
    version = 1
)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDatabaseDao: UserDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(): UserDatabase {
            synchronized(this) {
                if (userId == null) throw IllegalStateException("UserDatabase.getInstance() called with null authViewModel userId")
                var instance = INSTANCE
                if (instance == null) {
                    Log.d(TAG, "WARN: OPENING INSTANCE TO DATABASE")
                    // Opening a connection to a database is expensive!
                    instance = Room.databaseBuilder(
                        App.context,
                        UserDatabase::class.java,
                        "user_database_$userId"
                    )
                        .createFromAsset("database/preload.db")
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // Used when logging out
        fun dropInstance() {
            Log.d(TAG, "WARN: DATABASE INSTANCE DROPPED")
            INSTANCE = null
        }

    }
}
