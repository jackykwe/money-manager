package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.launch

private const val TAG = "tevm"

class TransactionEditViewModel(private val transactionId: Int): ViewModel() {

    private val userRepository = UserRepository.getInstance()

    private val _transaction = userRepository.getTransaction(transactionId)
    val transaction = MediatorLiveData<Transaction>().apply {
        value = Transaction(
            transactionId = null,
            timestamp = 0L,
            type = "",
            category = "",
            account = "",
            memo = "",
            originalCurrency = "",
            originalAmount = "0"  // Cannot be empty string!
        )
        addSource(_transaction) {
            value = _transaction.value
            _initShowContent.value = true
        }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            userRepository.deleteTransaction(transaction.value!!)
            _navigateUp.value = true
        }
    }

    private val _initShowContent = MutableLiveData2(false)
    val initShowContent: LiveData<Boolean>
        get() = _initShowContent

    fun initShowContentHandled() {
        _initShowContent.value = false
    }

    private val _navigateUp = MutableLiveData2(false)
    val navigateUp: LiveData<Boolean>
        get() = _navigateUp

    fun navigateUpHandled() {
        _navigateUp.value = false
    }

}