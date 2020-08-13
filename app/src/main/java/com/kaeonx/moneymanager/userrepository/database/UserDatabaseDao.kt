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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransaction(databaseTransaction: DatabaseTransaction)

    @Delete
    suspend fun deleteTransaction(databaseTransaction: DatabaseTransaction)

    @Query("UPDATE transactions_table SET category = :newCategoryName WHERE category = :oldCategoryName AND type = :type")
    suspend fun updateTransactionsRenameCategory(
        type: String,
        oldCategoryName: String,
        newCategoryName: String
    )

    @Query("UPDATE transactions_table SET account = :newAccountName WHERE account = :oldAccountName")
    suspend fun updateTransactionsRenameAccount(oldAccountName: String, newAccountName: String)

//    @Query("SELECT * FROM transactions_table ORDER BY timestamp DESC")
//    fun getAllTransactions(): LiveData<List<DatabaseTransaction>>

    @Query("SELECT * FROM transactions_table WHERE transactionId = :transactionId")
    fun getTransaction(transactionId: Int): LiveData<DatabaseTransaction>

    @Query("SELECT * FROM transactions_table WHERE timestamp BETWEEN :startMillis AND :endMillis ORDER BY timestamp DESC")
    fun getTransactionsBetween(
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<DatabaseTransaction>>

    @Query("SELECT * FROM transactions_table WHERE (timestamp BETWEEN :startMillis AND :endMillis) AND (type = :type) ORDER BY timestamp DESC")
    suspend fun getTypeTransactionsBetweenSuspend(
        type: String,
        startMillis: Long,
        endMillis: Long
    ): List<DatabaseTransaction>

    @Query("SELECT * FROM transactions_table WHERE (timestamp BETWEEN :startMillis AND :endMillis) AND (category = :category) AND (type = :type) ORDER BY timestamp DESC")
    suspend fun getCategoryTransactionsBetweenSuspend(
        type: String,
        category: String,
        startMillis: Long,
        endMillis: Long
    ): List<DatabaseTransaction>

    @Query("SELECT * FROM transactions_table WHERE (timestamp BETWEEN :startMillis AND :endMillis) AND (category = :category) AND (type = :type) ORDER BY timestamp DESC")
    fun getCategoryTransactionsBetween(
        type: String,
        category: String,
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<DatabaseTransaction>>

    @Query("SELECT * FROM transactions_table WHERE memo LIKE '%' || :memoQuery || '%'")
    fun searchTransactions(memoQuery: String): LiveData<List<DatabaseTransaction>>

    @Query("SELECT * FROM transactions_table")
    suspend fun exportTransactionsSuspend(): List<DatabaseTransaction>

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

    @Query("SELECT * FROM accounts_table")
    suspend fun exportAccountsSuspend(): List<DatabaseAccount>

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

    @Query("SELECT * FROM categories_table")
    suspend fun exportCategoriesSuspend(): List<DatabaseCategory>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Budget
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(databaseBudget: DatabaseBudget)

    @Query("SELECT * FROM budgets_table WHERE category = :category")
    fun getBudget(category: String): LiveData<DatabaseBudget?>

    @Query("SELECT * FROM budgets_table ORDER BY category")
    fun getAllBudgets(): LiveData<List<DatabaseBudget>>

    @Delete
    suspend fun deleteBudget(databaseBudget: DatabaseBudget)

    @Query("SELECT * FROM budgets_table")
    suspend fun exportBudgetsSuspend(): List<DatabaseBudget>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Preferences
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreference(databasePreference: DatabasePreference)

    @Query("SELECT * FROM preferences_table")
    fun getAllPreferences(): LiveData<List<DatabasePreference>>

    @Query("SELECT * FROM preferences_table")
    suspend fun exportPreferencesSuspend(): List<DatabasePreference>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Backup
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Query("DELETE FROM transactions_table")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM categories_table")
    suspend fun deleteAllCategories()

    @Query("DELETE FROM accounts_table")
    suspend fun deleteAllAccounts()

    @Query("DELETE FROM budgets_table")
    suspend fun deleteAllBudgets()

    @Query("DELETE FROM preferences_table")
    suspend fun deleteAllPreferences()

    @Insert
    suspend fun insertTransaction(databaseTransaction: DatabaseTransaction)

    @Insert
    suspend fun insertCategory(databaseCategory: DatabaseCategory)

    @Insert
    suspend fun insertAccount(databaseAccount: DatabaseAccount)

    @Insert
    suspend fun insertBudget(databaseBudget: DatabaseBudget)

    @Insert
    suspend fun insertPreference(databasePreference: DatabasePreference)

    @Transaction
    suspend fun overwriteDatabase(
        transactionsList: List<DatabaseTransaction>,
        categoriesList: List<DatabaseCategory>,
        accountsList: List<DatabaseAccount>,
        budgetsList: List<DatabaseBudget>,
        preferencesList: List<DatabasePreference>
    ) {
        deleteAllTransactions()
        transactionsList.forEach { insertTransaction(it) }
        deleteAllCategories()
        categoriesList.forEach { insertCategory(it) }
        deleteAllAccounts()
        accountsList.forEach { insertAccount(it) }
        deleteAllBudgets()
        budgetsList.forEach { insertBudget(it) }
        deleteAllPreferences()
        preferencesList.forEach { insertPreference(it) }
    }
}