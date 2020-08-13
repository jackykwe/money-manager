package com.kaeonx.moneymanager.userrepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.userrepository.database.toDomain
import com.kaeonx.moneymanager.userrepository.database.toMap
import com.kaeonx.moneymanager.userrepository.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "repository"

class UserRepository private constructor() {

    // TODO: Check for security holes
    private val database = UserDatabase.getInstance()

//    private val _transactions = database.userDatabaseDao.getAllTransactions()
//    val transactions = Transformations.map(_transactions) { it.toDomain() }

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

    internal suspend fun upsertTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertTransaction(transaction.toDatabase())
        }
    }

    internal suspend fun deleteTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteTransaction(transaction.toDatabase())
        }
    }

    internal suspend fun updateTransactionsRenameCategory(
        type: String,
        oldCategoryName: String,
        newCategoryName: String
    ) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.updateTransactionsRenameCategory(
                type,
                oldCategoryName,
                newCategoryName
            )
        }
    }

    internal suspend fun updateTransactionsRenameAccount(
        oldAccountName: String,
        newAccountName: String
    ) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.updateTransactionsRenameAccount(oldAccountName, newAccountName)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Accounts
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal suspend fun upsertAccount(account: Account) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertAccount(account.toDatabase())
        }
    }

    internal suspend fun deleteAccount(account: Account) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteAccount(account.toDatabase())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Categories
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal suspend fun upsertCategory(category: Category) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertCategory(category.toDatabase())
        }
    }

    internal suspend fun deleteCategory(category: Category) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteCategory(category.toDatabase())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Budget
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal suspend fun upsertBudget(budget: Budget) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertBudget(budget.toDatabase())
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

    internal suspend fun deleteBudget(budget: Budget) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteBudget(budget.toDatabase())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Preferences
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal suspend fun upsertPreference(preference: Preference) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertPreference(preference.toDatabase())
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

    internal suspend fun overwriteDatabase(
        transactionsList: List<Transaction>,
        categoriesList: List<Category>,
        accountsList: List<Account>,
        budgetsList: List<Budget>,
        preferencesList: List<Preference>
    ) = withContext(Dispatchers.IO) {
        database.userDatabaseDao.overwriteDatabase(
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
                if (userId == null) throw IllegalStateException("UserDatabase.getInstance() called with null authViewModel userId")  // TODO: relaunch after inactivity results in this error // This error occurs from anywhere - anywhere where UserRepository.getInstance() is requested. Typically on current screen ViewModel inits.
                var instance = INSTANCE
                if (instance == null) {
                    Log.d(TAG, "WARN: OPENING INSTANCE TO REPOSITORY")
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