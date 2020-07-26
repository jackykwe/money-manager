package com.kaeonx.moneymanager.userrepository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.userrepository.database.toDomain
import com.kaeonx.moneymanager.userrepository.domain.Account
import com.kaeonx.moneymanager.userrepository.domain.Category
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.userrepository.domain.toDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(application: Application, userId: String) {

    // TODO: Check for security holes
    private val database = UserDatabase.getInstance(application, userId)

    private val _transactions = database.userDatabaseDao.getAllTransactions()
    val transactions: LiveData<List<Transaction>> = Transformations.map(_transactions) { it.toDomain() }

    private val _accounts = database.userDatabaseDao.getAllAccounts()
    val accounts: LiveData<List<Account>> = Transformations.map(_accounts) { it.toDomain() }

    private val _incomeCategories = database.userDatabaseDao.getAllIncomeCategories()
    val incomeCategories: LiveData<List<Category>> = Transformations.map(_incomeCategories) { it.toDomain() }

    private val _expensesCategories = database.userDatabaseDao.getAllExpensesCategories()
    val expensesCategories: LiveData<List<Category>> = Transformations.map(_expensesCategories) { it.toDomain() }

    suspend fun addTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.insertTransaction(transaction.toDatabase())
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.userDatabaseDao.updateTransaction(transaction.toDatabase())
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

}