package com.kaeonx.moneymanager.userrepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.userrepository.database.toDomain
import com.kaeonx.moneymanager.userrepository.database.toMap
import com.kaeonx.moneymanager.userrepository.domain.Account
import com.kaeonx.moneymanager.userrepository.domain.Category
import com.kaeonx.moneymanager.userrepository.domain.Preference
import com.kaeonx.moneymanager.userrepository.domain.Transaction
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
    fun getTransaction(transactionId: Int): LiveData<Transaction> =
        Transformations.map(database.userDatabaseDao.getTransaction(transactionId)) {
            it.toDomain()
        }

    fun getTransactionsBetween(startMillis: Long, endMillis: Long): LiveData<List<Transaction>> =
        Transformations.map(
            database.userDatabaseDao.getTransactionsBetween(
                startMillis,
                endMillis
            )
        ) {
            it.toDomain()
        }

    suspend fun upsertTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertTransaction(transaction.toDatabase())
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteTransaction(transaction.toDatabase())
        }
    }

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.clearAllData()
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Accounts
     */
    ////////////////////////////////////////////////////////////////////////////////
    suspend fun upsertAccount(account: Account) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertAccount(account.toDatabase())
        }
    }

    suspend fun deleteAccount(account: Account) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteAccount(account.toDatabase())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Categories
     */
    ////////////////////////////////////////////////////////////////////////////////
    suspend fun upsertCategory(category: Category) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertCategory(category.toDatabase())
        }
    }

    suspend fun deleteCategory(category: Category) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.deleteCategory(category.toDatabase())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Preferences
     */
    ////////////////////////////////////////////////////////////////////////////////
    suspend fun upsertPreference(preference: Preference) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.upsertPreference(preference.toDatabase())
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(): UserRepository {
            synchronized(this) {
                if (userId == null) throw IllegalStateException("UserDatabase.getInstance() called with null authViewModel userId")
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