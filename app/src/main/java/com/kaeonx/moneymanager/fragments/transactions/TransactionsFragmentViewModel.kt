package com.kaeonx.moneymanager.fragments.transactions

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import kotlinx.coroutines.launch

private const val TAG = "tfvm"

class TransactionsFragmentViewModel : ViewModel() {
    init {
        Log.d(TAG, "TFVM started, with userId $userId")
    }

    private val userRepository = UserRepository.getInstance()

    //    val dayTransactions: LiveData<List<DayTransactions>> = Transformations.map(userRepository.transactions) {
//        it.toDayTransactions()
//    }
    private val _transactions = userRepository.transactions
    val dayTransactions = Transformations.map(_transactions) { it.toDayTransactions() }
    fun calculateDayTransactions(): List<DayTransactions> =
        _transactions.value?.toDayTransactions() ?: listOf()

    val preferences = userRepository.preferences


    fun clearAllData() {
        viewModelScope.launch {
            userRepository.clearAllData()
        }
    }
}