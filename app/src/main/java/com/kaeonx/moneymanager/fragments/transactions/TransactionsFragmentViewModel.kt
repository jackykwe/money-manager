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

    private val userRepository = UserRepository.getInstance(application, userId)
    val dayTransactions: LiveData<List<DayTransactions>> = Transformations.map(userRepository.transactions) {
        it.toDayTransactions("SGD")
    }
    val homeCurrency = MutableLiveData<String>("SGD")

    fun addTransaction(transaction: Transaction) {
        Log.d(TAG, "addTransaction: called")
        viewModelScope.launch {
            userRepository.addTransaction(transaction)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            userRepository.clearAllData()
        }
    }
}