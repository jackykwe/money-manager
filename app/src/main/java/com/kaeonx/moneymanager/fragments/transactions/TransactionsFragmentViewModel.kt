package com.kaeonx.moneymanager.fragments.transactions

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.txnrepository.TxnRepository
import com.kaeonx.moneymanager.txnrepository.domain.Transaction
import kotlinx.coroutines.launch

private const val TAG = "tfvm"

class TransactionsFragmentViewModel(application: Application, userId: String) :
    AndroidViewModel(application) {
    init {
        Log.d(TAG, "TFVM started, with userId $userId")
    }

//    val mainText = MutableLiveData<String>("HELLO TRANS FRAG")

    private val txnRepository = TxnRepository(application, userId)
    private val txns = txnRepository.txns
    val mainText: LiveData<String> = Transformations.map(txns) {
        txns.value.toString()
    }

    fun addTxn() {
        viewModelScope.launch {
            txnRepository.addTxn(
                Transaction(
                    timestamp = 12345,
                    timeZone = "Singapore",
                    type = "Expenses",
                    category = "Yes",
                    account = "Cash",
                    memo = "MemoSample",
                    originalCurrency = "SGD",
                    originalAmount = "1.05"
                )
            )
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            txnRepository.clearAllData()
        }
    }
}