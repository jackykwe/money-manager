package com.kaeonx.moneymanager.userrepository.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.activities.App

@Database(
    entities = [
        DatabaseBudget::class,
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

//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                Log.d(TAG, "Migration 1 --> 2 happening!")
//                database.execSQL("CREATE TABLE IF NOT EXISTS `budgets_table` (`category` TEXT NOT NULL, `original_currency` TEXT NOT NULL, `original_amount` TEXT NOT NULL, PRIMARY KEY(`category`))")
//                Log.d(TAG, "Migration 1 --> 2 done!")
//            }
//        }

        @Volatile
        private var INSTANCE: UserDatabase? = null

        @Volatile
        private var INSTANCE_USER_ID: String? = null

        fun getInstance(): UserDatabase {
            synchronized(this) {
                if (Firebase.auth.currentUser?.uid == null) throw IllegalStateException("UserDatabase.getInstance() called with null authViewModel userId")
                var instance = INSTANCE
                if (instance == null) {
                    // Opening a connection to a database is expensive!
                    instance = Room.databaseBuilder(
                        App.context,
                        UserDatabase::class.java,
                        "user_database_${Firebase.auth.currentUser!!.uid}"
                    )
//                        .addMigrations(MIGRATION_1_2)
                        .createFromAsset("database/preload.db")
                        .build()
                    INSTANCE = instance
                    INSTANCE_USER_ID = Firebase.auth.currentUser!!.uid
                } else if (Firebase.auth.currentUser!!.uid != INSTANCE_USER_ID) {
                    throw IllegalStateException("UserDatabase.getInstance() called with different userId")
                }
                return instance
            }
        }

        // Used when logging out
        fun dropInstance() {
            INSTANCE?.let {
                if (it.isOpen) it.close()
            }
            INSTANCE = null
            INSTANCE_USER_ID = null
        }

    }
}
