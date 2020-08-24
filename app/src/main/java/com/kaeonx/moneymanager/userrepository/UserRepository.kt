package com.kaeonx.moneymanager.userrepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.userrepository.database.toDomain
import com.kaeonx.moneymanager.userrepository.database.toMap
import com.kaeonx.moneymanager.userrepository.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "repository"

class UserRepository private constructor() {

    private val database = UserDatabase.getInstance()

    private val _accounts = database.userDatabaseDao.getAllAccounts()
    val accounts = Transformations.map(_accounts) { it.toDomain() }

    private val _categories = database.userDatabaseDao.getAllCategories()
    val categories = Transformations.map(_categories) { it.toDomain() }

    private val _preferences = database.userDatabaseDao.getAllPreferences()
    val preferences = Transformations.map(_preferences) { it.toMap() }

    private val liveDataActivator = Observer<Any> { }

    init {
        // These values are observed statically (they are accessed by items not bound to
        // views like transactions in TransactionsFragment - e.g. Transaction.toDetail()).
        // We need to call these functions to make them alive.
        // (Try removing this and the icons won't load in Transactions Fragment at first load.
        accounts.observeForever(liveDataActivator)
        categories.observeForever(liveDataActivator)
        preferences.observeForever(liveDataActivator)
    }

    private fun clearPermanentObservers() {
        accounts.removeObserver(liveDataActivator)
        categories.removeObserver(liveDataActivator)
        preferences.removeObserver(liveDataActivator)
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Transaction
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal fun getTransaction(transactionId: Int): LiveData<Transaction> =
        Transformations.map(database.userDatabaseDao.getTransaction(transactionId)) {
            it.toDomain()
        }

    internal fun getTransactionsBetween(
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<Transaction>> =
        Transformations.map(
            database.userDatabaseDao.getTransactionsBetween(
                startMillis,
                endMillis
            )
        ) {
            it.toDomain()
        }

    internal suspend fun getTransactionsBetweenSuspend(
        type: String,
        category: String,
        startMillis: Long,
        endMillis: Long
    ): List<Transaction> =
        withContext(Dispatchers.IO) {
            when (category) {
                "Overall" -> database.userDatabaseDao.getTypeTransactionsBetweenSuspend(
                    type,
                    startMillis,
                    endMillis
                ).toDomain()
                else -> database.userDatabaseDao.getCategoryTransactionsBetweenSuspend(
                    type,
                    category,
                    startMillis,
                    endMillis
                ).toDomain()
            }
        }


    internal fun getCategoryTransactionsBetween(
        type: String,
        category: String,
        startMillis: Long,
        endMillis: Long
    ): LiveData<List<Transaction>> =
        Transformations.map(
            database.userDatabaseDao.getCategoryTransactionsBetween(
                type,
                category,
                startMillis,
                endMillis
            )
        ) { it.toDomain() }

    internal fun searchTransactions(memoQuery: String): LiveData<List<Transaction>> =
        Transformations.map(database.userDatabaseDao.searchTransactions(memoQuery)) {
            it.toDomain()
        }

    internal suspend fun upsertTransactionSuspend(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertTransactionSuspend(transaction.toDatabase())
        }
    }

    internal suspend fun deleteTransactionSuspend(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteTransactionSuspend(transaction.toDatabase())
        }
    }

    internal suspend fun deleteTransactionsTransactionSuspend(transactionIds: List<Int>) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteTransactionsByIdTransactionSuspend(transactionIds)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Accounts
     */
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Upserts the Account, and updates all Transactions (if applicable) bearing
     * the same name as oldAccountName. Also updates Preference tst_default_account
     * (if applicable). This is done in an SQL transaction.
     */
    internal suspend fun upsertAccountTransactionSuspend(
        newAccount: Account,
        oldAccountName: String,
        updateTstDefaultAccount: Boolean
    ) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertAccountTransactionSuspend(
                databaseAccount = newAccount.toDatabase(),
                oldAccountName = oldAccountName,
                updateTstDefaultAccount = updateTstDefaultAccount
            )
        }
    }

    /**
     * Deletes the Account. Also updates Preference tst_default_account (if
     * applicable). This is done in an SQL transaction.
     */
    internal suspend fun deleteAccountTransactionSuspend(
        account: Account,
        updateTstDefaultAccount: Boolean,
        newTstDefaultAccount: String
    ) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteAccountTransactionSuspend(
                databaseAccount = account.toDatabase(),
                updateTstDefaultAccount = updateTstDefaultAccount,
                newTstDefaultAccount = newTstDefaultAccount
            )
        }
    }

    internal suspend fun deleteAccountsTransactionSuspend(
        accountIds: List<Int>,
        updateTstDefaultAccount: Boolean,
        newTstDefaultAccount: String
    ) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteAccountsByIdTransactionSuspend(
                accountIds = accountIds,
                updateTstDefaultAccount = updateTstDefaultAccount,
                newTstDefaultAccount = newTstDefaultAccount
            )
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Categories
     */
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Upserts the Category, and updates all Transactions (if applicable) bearing
     * the same name as oldCategoryName. Also updates the Budget bearing the same
     * name (if applicable). This is done in an SQL transaction.
     */
    internal suspend fun upsertCategoryTransactionSuspend(
        newCategory: Category,
        oldCategoryName: String
    ) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertCategoryTransactionSuspend(
                databaseCategory = newCategory.toDatabase(),
                oldCategoryName = oldCategoryName
            )
        }
    }

    internal suspend fun deleteCategorySuspend(category: Category) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteCategorySuspend(category.toDatabase())
        }
    }

    internal suspend fun deleteCategoriesTransactionSuspend(categoryIds: List<Int>) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteCategoriesByIdTransactionSuspend(categoryIds)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Budget
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal suspend fun upsertBudgetSuspend(budget: Budget) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertBudgetSuspend(budget.toDatabase())
        }
    }

    internal fun getBudget(category: String): LiveData<Budget?> =
        Transformations.map(database.userDatabaseDao.getBudget(category)) {
            it?.toDomain()
        }

    internal fun getAllBudgets(): LiveData<List<Budget>> =
        Transformations.map(database.userDatabaseDao.getAllBudgets()) {
            it.toDomain()
        }

    internal suspend fun deleteBudgetSuspend(budget: Budget) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteBudgetSuspend(budget.toDatabase())
        }
    }

    internal suspend fun deleteBudgetsTransactionSuspend(categories: List<String>) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteBudgetsByIdTransactionSuspend(categories)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Preferences
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal suspend fun upsertPreferenceSuspend(preference: Preference) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertPreferenceSuspend(preference.toDatabase())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Backup
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal suspend fun exportTransactionsSuspend(): List<Transaction> =
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.exportTransactionsSuspend().toDomain()
        }

    internal suspend fun exportBudgetsSuspend(): List<Budget> =
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.exportBudgetsSuspend().toDomain()
        }

    internal suspend fun exportCategoriesSuspend(): List<Category> =
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.exportCategoriesSuspend().toDomain()
        }

    internal suspend fun exportAccountsSuspend(): List<Account> =
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.exportAccountsSuspend().toDomain()
        }

    internal suspend fun exportPreferencesSuspend(): List<Preference> =
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.exportPreferencesSuspend().toDomain()
        }

    internal suspend fun overwriteDatabaseTransactionSuspend(
        transactionsList: List<Transaction>,
        categoriesList: List<Category>,
        accountsList: List<Account>,
        budgetsList: List<Budget>,
        preferencesList: List<Preference>
    ) = withContext(Dispatchers.IO) {
        database.userDatabaseDao.overwriteDatabaseTransactionSuspend(
            transactionsList.toDatabase(),
            categoriesList.toDatabase(),
            accountsList.toDatabase(),
            budgetsList.toDatabase(),
            preferencesList.toDatabase()
        )
    }

    companion object {

        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(): UserRepository {
            synchronized(this) {
                if (Firebase.auth.currentUser?.uid == null) throw IllegalStateException("UserDatabase.getInstance() called with null authViewModel userId")
                var instance = INSTANCE
                if (instance == null) {
                    Log.d(
                        TAG,
                        "WARN: OPENING INSTANCE TO REPOSITORY FOR ${Firebase.auth.currentUser!!.uid}"
                    )
                    // Opening a connection to a database is expensive!
                    instance = UserRepository()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // Used when logging out
        fun dropInstance() {
            Log.d(TAG, "WARN: REPOSITORY INSTANCE DROPPED")
            INSTANCE?.clearPermanentObservers()
            INSTANCE = null
        }
    }
}