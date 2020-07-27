package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class TransactionsFragmentViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsFragmentViewModel::class.java)) {
            return TransactionsFragmentViewModel(userId) as T
        }
        throw IllegalArgumentException("Unable to instantiate TransactionsFragmentViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}