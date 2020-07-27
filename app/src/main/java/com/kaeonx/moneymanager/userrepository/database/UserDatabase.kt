package com.kaeonx.moneymanager.userrepository.database

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kaeonx.moneymanager.activities.App

private const val TAG = "dtb"

@Database(
    entities = [
        DatabaseTransaction::class,
        DatabaseAccount::class,
        DatabaseIncomeCategory::class,
        DatabaseExpensesCategory::class
    ],
    version = 1
)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDatabaseDao: UserDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(userID: String): UserDatabase {
            Log.d(TAG, "getInstance: called")
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    Log.d(TAG, "WARN: OPENING INSTANCE TO DATABASE")
                    // Opening a connection to a database is expensive!
                    instance = Room.databaseBuilder(
                        App.context,
                        UserDatabase::class.java,
                        "user_database_$userID"
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
            INSTANCE = null
        }

    }
}
