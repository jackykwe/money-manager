package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class TransactionEditViewModelFactory(private val transactionId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionEditViewModel::class.java)) {
            return TransactionEditViewModel(transactionId) as T
        }
        throw IllegalArgumentException("Unable to instantiate TransactionEditViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}