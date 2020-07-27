package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaeonx.moneymanager.userrepository.domain.Transaction

@Suppress("UNCHECKED_CAST")
class TransactionsBSDFViewModelFactory(private val oldTransaction: Transaction) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsBSDFViewModel::class.java)) {
            return TransactionsBSDFViewModel(oldTransaction) as T
        }
        throw IllegalArgumentException("Unable to instantiate TransactionsBSDFViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}