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
    suspend fun upsertTransactionSuspend(databaseTransaction: DatabaseTransaction)

    @Delete
    suspend fun deleteTransactionSuspend(databaseTransaction: DatabaseTransaction)

    @Query("DELETE FROM transactions_table WHERE transactionId = :transactionId")
    suspend fun deleteTransactionByIdSuspend(transactionId: Int)

    @Transaction
    suspend fun deleteTransactionsByIdTransactionSuspend(transactionIds: List<Int>) {
        transactionIds.forEach { deleteTransactionByIdSuspend(it) }
    }

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

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Accounts
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccountSuspend(databaseAccount: DatabaseAccount)

    @Query("UPDATE transactions_table SET account = :newAccountName WHERE account = :oldAccountName")
    suspend fun updateTransactionsRenameAccountSuspend(
        oldAccountName: String,
        newAccountName: String
    )

    @Transaction
    suspend fun upsertAccountTransactionSuspend(
        databaseAccount: DatabaseAccount,
        oldAccountName: String,
        updateTstDefaultAccount: Boolean
    ) {
        upsertAccountSuspend(databaseAccount)
        updateTransactionsRenameAccountSuspend(
            oldAccountName = oldAccountName,
            newAccountName = databaseAccount.name
        )
        if (updateTstDefaultAccount) {
            upsertPreferenceSuspend(
                DatabasePreference(
                    key = "tst_default_account",
                    valueInteger = null,
                    valueText = databaseAccount.name
                )
            )
        }
    }

    @Delete
    suspend fun deleteAccountSuspend(databaseAccount: DatabaseAccount)

    @Transaction
    suspend fun deleteAccountTransactionSuspend(
        databaseAccount: DatabaseAccount,
        updateTstDefaultAccount: Boolean,
        newTstDefaultAccount: String
    ) {
        deleteAccountSuspend(databaseAccount)
        if (updateTstDefaultAccount) {
            upsertPreferenceSuspend(
                DatabasePreference(
                    key = "tst_default_account",
                    valueInteger = null,
                    valueText = newTstDefaultAccount
                )
            )
        }
    }

    @Query("SELECT * FROM accounts_table ORDER BY name COLLATE NOCASE")
    fun getAllAccounts(): LiveData<List<DatabaseAccount>>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Categories
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategorySuspend(databaseCategory: DatabaseCategory)

    @Query("UPDATE transactions_table SET category = :newCategoryName WHERE category = :oldCategoryName AND type = :type")
    suspend fun updateTransactionsRenameCategorySuspend(
        type: String,
        oldCategoryName: String,
        newCategoryName: String
    )

    @Query("UPDATE budgets_table SET category = :newCategoryName WHERE category = :oldCategoryName")
    suspend fun updateBudgetRenameCategorySuspend(
        oldCategoryName: String,
        newCategoryName: String
    )

    @Transaction
    suspend fun upsertCategoryTransactionSuspend(
        databaseCategory: DatabaseCategory,
        oldCategoryName: String
    ) {
        upsertCategorySuspend(databaseCategory)
        updateTransactionsRenameCategorySuspend(
            type = databaseCategory.type,
            oldCategoryName = oldCategoryName,
            newCategoryName = databaseCategory.name
        )
        updateBudgetRenameCategorySuspend(
            oldCategoryName = oldCategoryName,
            newCategoryName = databaseCategory.name
        )
    }

    @Delete
    suspend fun deleteCategorySuspend(databaseCategory: DatabaseCategory)

    @Query("DELETE FROM categories_table WHERE categoryId = :categoryId")
    suspend fun deleteCategoryByIdSuspend(categoryId: Int)

    @Transaction
    suspend fun deleteCategoriesByIdTransactionSuspend(categoryIds: List<Int>) {
        categoryIds.forEach { deleteCategoryByIdSuspend(it) }
    }

    @Query("SELECT * FROM categories_table ORDER BY name COLLATE NOCASE")
    fun getAllCategories(): LiveData<List<DatabaseCategory>>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Budget
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudgetSuspend(databaseBudget: DatabaseBudget)

    @Query("SELECT * FROM budgets_table WHERE category = :category")
    fun getBudget(category: String): LiveData<DatabaseBudget?>

    @Query("SELECT * FROM budgets_table ORDER BY category")
    fun getAllBudgets(): LiveData<List<DatabaseBudget>>

    @Delete
    suspend fun deleteBudgetSuspend(databaseBudget: DatabaseBudget)

    @Query("DELETE FROM budgets_table WHERE category = :category")
    suspend fun deleteBudgetByCategorySuspend(category: String)

    @Transaction
    suspend fun deleteBudgetsByIdTransactionSuspend(categories: List<String>) {
        categories.forEach { deleteBudgetByCategorySuspend(it) }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Preferences
     */
    ////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreferenceSuspend(databasePreference: DatabasePreference)

    @Query("SELECT * FROM preferences_table")
    fun getAllPreferences(): LiveData<List<DatabasePreference>>

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Backup
     */
    ////////////////////////////////////////////////////////////////////////////////

    @Query("SELECT * FROM transactions_table")
    suspend fun exportTransactionsSuspend(): List<DatabaseTransaction>

    @Query("SELECT * FROM accounts_table")
    suspend fun exportAccountsSuspend(): List<DatabaseAccount>

    @Query("SELECT * FROM categories_table")
    suspend fun exportCategoriesSuspend(): List<DatabaseCategory>

    @Query("SELECT * FROM budgets_table")
    suspend fun exportBudgetsSuspend(): List<DatabaseBudget>

    @Query("SELECT * FROM preferences_table")
    suspend fun exportPreferencesSuspend(): List<DatabasePreference>

    @Query("DELETE FROM transactions_table")
    suspend fun deleteAllTransactionsSuspend()

    @Query("DELETE FROM categories_table")
    suspend fun deleteAllCategoriesSuspend()

    @Query("DELETE FROM accounts_table")
    suspend fun deleteAllAccountsSuspend()

    @Query("DELETE FROM budgets_table")
    suspend fun deleteAllBudgetsSuspend()

    @Query("DELETE FROM preferences_table")
    suspend fun deleteAllPreferencesSuspend()

    @Insert
    suspend fun insertTransactionSuspend(databaseTransaction: DatabaseTransaction)

    @Insert
    suspend fun insertCategorySuspend(databaseCategory: DatabaseCategory)

    @Insert
    suspend fun insertAccountSuspend(databaseAccount: DatabaseAccount)

    @Insert
    suspend fun insertBudgetSuspend(databaseBudget: DatabaseBudget)

    @Insert
    suspend fun insertPreferenceSuspend(databasePreference: DatabasePreference)

    @Transaction
    suspend fun overwriteDatabaseTransactionSuspend(
        transactionsList: List<DatabaseTransaction>,
        categoriesList: List<DatabaseCategory>,
        accountsList: List<DatabaseAccount>,
        budgetsList: List<DatabaseBudget>,
        preferencesList: List<DatabasePreference>
    ) {
        deleteAllTransactionsSuspend()
        transactionsList.forEach { insertTransactionSuspend(it) }
        deleteAllCategoriesSuspend()
        categoriesList.forEach { insertCategorySuspend(it) }
        deleteAllAccountsSuspend()
        accountsList.forEach { insertAccountSuspend(it) }
        deleteAllBudgetsSuspend()
        budgetsList.forEach { insertBudgetSuspend(it) }
        deleteAllPreferencesSuspend()
        preferencesList.forEach { insertPreferenceSuspend(it) }
    }
}