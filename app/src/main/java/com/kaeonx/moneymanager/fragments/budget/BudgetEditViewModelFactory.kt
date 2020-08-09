package com.kaeonx.moneymanager.fragments.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaeonx.moneymanager.userrepository.domain.Budget

@Suppress("UNCHECKED_CAST")
class BudgetEditViewModelFactory(private val oldBudget: Budget) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetEditViewModel::class.java)) {
            return BudgetEditViewModel(oldBudget) as T
        }
        throw IllegalArgumentException("Unable to instantiate BudgetEditViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}