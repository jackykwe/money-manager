package com.kaeonx.moneymanager.fragments.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaeonx.moneymanager.userrepository.domain.Account

@Suppress("UNCHECKED_CAST")
class AccountEditViewModelFactory(private val oldAccount: Account) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountEditViewModel::class.java)) {
            return AccountEditViewModel(oldAccount) as T
        }
        throw IllegalArgumentException("Unable to instantiate AccountEditViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}