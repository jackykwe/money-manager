package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.userrepository.UserRepository
import kotlinx.coroutines.launch

private const val TAG = "tevm"

class TransactionEditViewModel(private val transactionId: Int): ViewModel() {

    private val userRepository = UserRepository.getInstance()
    val transaction = Transformations.map(userRepository.transactions) { list ->
        list.find { it.transactionId == transactionId }!!
    }

//    val transaction = userRepository.getTransaction(initTransaction.transactionId!!)

    fun deleteTransaction() {
        viewModelScope.launch {
            userRepository.deleteTransaction(transaction.value!!)
            _navigateUp.value = true
        }
    }

    private val _navigateUp = MutableLiveData2(false)
    val navigateUp : LiveData<Boolean>
        get() = _navigateUp
    fun navigateUpHandled() {
        _navigateUp.value = false
    }

}