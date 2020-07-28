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
    @Insert
    suspend fun insertAccount(databaseAccount: DatabaseAccount)

    @Update
    suspend fun updateAccount(databaseAccount: DatabaseAccount)  // Key must be the same

    @Delete
    suspend fun deleteAccount(databaseAccount: DatabaseAccount)

    // TODO: RESET ACCOUNTS TO PRESET: Delete and overwrite in a database Transaction
//    @Query("DELETE FROM transactions_table")
//    suspend fun clearAllData()

    @Query("SELECT * FROM accounts_table")
    fun getAllAccounts(): LiveData<List<DatabaseAccount>>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Categories
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertIncomeCategory(databaseIncomeCategory: DatabaseIncomeCategory)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExpensesCategory(databaseExpensesCategory: DatabaseExpensesCategory)

    @Delete
    suspend fun deleteIncomeCategory(databaseIncomeCategory: DatabaseIncomeCategory)
    @Delete
    suspend fun deleteExpensesCategory(databaseExpensesCategory: DatabaseExpensesCategory)

    // TODO: RESET ACCOUNTS TO PRESET: Delete and overwrite in a database Transaction
//    @Query("DELETE FROM transactions_table")
//    suspend fun clearAllData()

    @Query("SELECT * FROM income_categories_table")
    fun getAllIncomeCategories(): LiveData<List<DatabaseIncomeCategory>>
    @Query("SELECT * FROM expenses_categories_table")
    fun getAllExpensesCategories(): LiveData<List<DatabaseExpensesCategory>>
}