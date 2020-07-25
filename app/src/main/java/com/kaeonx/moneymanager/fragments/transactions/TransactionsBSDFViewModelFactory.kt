package com.kaeonx.moneymanager.fragments.transactions

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaeonx.moneymanager.txnrepository.domain.Transaction

@Suppress("UNCHECKED_CAST")
class TransactionsBSDFViewModelFactory(private val application: Application, private val oldTransaction: Transaction) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsBSDFViewModel::class.java)) {
            return TransactionsBSDFViewModel(application, oldTransaction) as T
        }
        throw IllegalArgumentException("Unable to instantiate TransactionsBSDFViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}