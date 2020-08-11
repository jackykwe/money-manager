package com.kaeonx.moneymanager.fragments.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

@Suppress("UNCHECKED_CAST")
class BudgetDetailViewModelFactory(
    private val category: String,
    private val initCalendar: Calendar
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetDetailViewModel::class.java)) {
            return BudgetDetailViewModel(category, initCalendar) as T
        }
        throw IllegalArgumentException("Unable to instantiate BudgetDetailViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}