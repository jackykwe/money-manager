package com.kaeonx.moneymanager.fragments.transactions

import android.util.Log
import androidx.lifecycle.MediatorLiveData
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
    private val _transactions = userRepository.transactions
    val sensitiveDayTransactions = MediatorLiveData<List<DayTransactions>>().apply {
        addSource(_transactions) { value = updateDayTransactions() }
        addSource(userRepository.preferences) { value = updateDayTransactions() }
        // TODO: Add XE table to the source too. If change, update.
    }

    private fun updateDayTransactions(): List<DayTransactions> =
        _transactions.value?.toDayTransactions() ?: listOf()


    fun clearAllData() {
        viewModelScope.launch {
            userRepository.clearAllData()
        }
    }
}