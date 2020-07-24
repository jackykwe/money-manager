package com.kaeonx.moneymanager.txnrepository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kaeonx.moneymanager.txnrepository.database.TxnDatabase
import com.kaeonx.moneymanager.txnrepository.database.toDomain
import com.kaeonx.moneymanager.txnrepository.domain.Transaction
import com.kaeonx.moneymanager.txnrepository.domain.toDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TxnRepository(application: Application, userId: String) {

    // TODO: Check for security holes
    private val database = TxnDatabase.getInstance(application, userId)

    private val _txns = database.txnDatabaseDao.getAllTxns()
    val txns: LiveData<List<Transaction>> = Transformations.map(_txns) { it.toDomain() }

    suspend fun addTxn(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.txnDatabaseDao.insertTxn(transaction.toDatabase())
        }
    }

    suspend fun updateTxn(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.txnDatabaseDao.updateTxn(transaction.toDatabase())
        }
    }

    suspend fun deleteTxn(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            database.txnDatabaseDao.deleteTxn(transaction.toDatabase())
        }
    }

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            database.txnDatabaseDao.clearAllData()
        }
    }

}