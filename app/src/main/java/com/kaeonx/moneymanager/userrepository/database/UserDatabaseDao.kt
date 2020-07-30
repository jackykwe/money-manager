package com.kaeonx.moneymanager.userrepository.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDatabaseDao {

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Transactions
     */
    ////////////////////////////////////////////////////////////////////////////////

    @Insert
    suspend fun insertTransaction(databaseTransaction: DatabaseTransaction)

    @Update
    suspend fun updateTransaction(databaseTransaction: DatabaseTransaction)  // Key must be the same

    @Delete
    suspend fun deleteTransaction(databaseTransaction: DatabaseTransaction)

    // TODO: WARN USER IF THEY WILL PERFORM THIS ACTION
    @Query("DELETE FROM transactions_table")
    suspend fun clearAllData()

    @Query("SELECT * FROM transactions_table")
    fun getAllTransactions(): LiveData<List<DatabaseTransaction>>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Accounts
     */
    ////////////////////////////////////////////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccount(databaseAccount: DatabaseAccount)

    @Delete
    suspend fun deleteAccount(databaseAccount: DatabaseAccount)

    // TODO: RESET ACCOUNTS TO PRESET: Delete and overwrite in a database Transaction
//    @Query("DELETE FROM transactions_table")
//    suspend fun clearAllData()

    @Query("SELECT * FROM accounts_table ORDER BY name COLLATE NOCASE")
    fun getAllAccounts(): LiveData<List<DatabaseAccount>>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Categories
     */
    ////////////////////////////////////////////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(databaseCategory: DatabaseCategory)

    @Delete
    suspend fun deleteCategory(databaseCategory: DatabaseCategory)

    // TODO: RESET ACCOUNTS TO PRESET: Delete and overwrite in a database Transaction
//    @Query("DELETE FROM transactions_table")
//    suspend fun clearAllData()

    @Query("SELECT * FROM categories_table ORDER BY name COLLATE NOCASE")
    fun getAllCategories(): LiveData<List<DatabaseCategory>>


    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Preferences
     */
    ////////////////////////////////////////////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreference(databasePreference: DatabasePreference)

    @Query("SELECT * FROM preferences_table")
    fun getAllPreferences(): LiveData<List<DatabasePreference>>
}