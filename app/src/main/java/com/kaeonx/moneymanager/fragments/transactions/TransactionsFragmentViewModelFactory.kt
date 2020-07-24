package com.kaeonx.moneymanager.fragments.transactions

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class TransactionsFragmentViewModelFactory(private val application: Application, private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsFragmentViewModel::class.java)) {
            return TransactionsFragmentViewModel(application, userId) as T
        }
        throw IllegalArgumentException("Unable to instantiate TransactionsFragmentViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}