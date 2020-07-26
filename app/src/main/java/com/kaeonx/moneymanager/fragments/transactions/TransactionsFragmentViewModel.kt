package com.kaeonx.moneymanager.fragments.transactions

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import kotlinx.coroutines.launch

private const val TAG = "tfvm"

class TransactionsFragmentViewModel(application: Application, userId: String) : AndroidViewModel(application) {
    init {
        Log.d(TAG, "TFVM started, with userId $userId")
    }

//    val mainText = MutableLiveData<String>("HELLO TRANS FRAG")

    private val userRepository = UserRepository(application, userId)
    val dayTransactions: LiveData<List<DayTransactions>> = Transformations.map(userRepository.transactions) {
        it.toDayTransactions("SGD")
    }
    val homeCurrency = MutableLiveData<String>("SGD")

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            userRepository.addTransaction(transaction)
        }
    }

    fun addTransactionFixed() {
        addTransaction(
            Transaction(
                timestamp = 12345,
                type = "Expenses",
                category = "Yes",
                account = "Cash",
                memo = "MemoSample",
                originalCurrency = "SGD",
                originalAmount = "1.05"
            )
        )
    }

    fun clearAllData() {
        viewModelScope.launch {
            userRepository.clearAllData()
        }
    }
}