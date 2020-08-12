package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class TransactionsSearchViewModelFactory(private val initQuery: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsSearchViewModel::class.java)) {
            return TransactionsSearchViewModel(initQuery) as T
        }
        throw IllegalArgumentException("Unable to instantiate TransactionsSearchViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}